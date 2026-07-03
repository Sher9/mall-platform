package com.example.mall.cart

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.mall.base.router.RouterPath
import com.example.mall.cart.adapter.CartAdapter
import com.example.mall.cart.databinding.ActivityCartBinding
import com.example.mall.cart.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 购物车页面
 * 路由路径：/cart/main
 */
@Route(path = RouterPath.Cart.CART)
@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // 加载购物车数据
        viewModel.loadCartItems()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onItemCheckChanged = { cartItem, isChecked ->
                viewModel.updateCartItemChecked(cartItem.id, isChecked)
            },
            onQuantityChanged = { cartItem, quantity ->
                viewModel.updateCartItemQuantity(cartItem.id, quantity)
            },
            onItemDeleted = { cartItem ->
                viewModel.deleteCartItem(cartItem.id)
            }
        )

        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun setupObservers() {
        // 观察购物车列表
        viewModel.cartItems.observe(this) { items ->
            cartAdapter.submitList(items)
            updateCartSummary(items)
        }

        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun setupClickListeners() {
        // 下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadCartItems()
        }

        // 全选按钮
        binding.selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.selectAllItems(isChecked)
        }

        // 去结算按钮
        binding.checkoutButton.setOnClickListener {
            val selectedItems = viewModel.getSelectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "请选择要结算的商品", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 跳转到确认订单页
        }
    }

    private fun updateCartSummary(items: List<CartItem>) {
        val selectedItems = items.filter { it.isChecked }
        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        val totalCount = selectedItems.sumOf { it.quantity }

        binding.totalPriceTextView.text = "¥${String.format("%.2f", totalPrice)}"
        binding.totalCountTextView.text = "共${totalCount}件"
        binding.checkoutButton.isEnabled = selectedItems.isNotEmpty()
    }
}

/**
 * 购物车适配器
 */
class CartAdapter(
    private val onItemCheckChanged: (CartItem, Boolean) -> Unit,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemDeleted: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                checkBox.isChecked = cartItem.isChecked
                productNameTextView.text = cartItem.productName
                priceTextView.text = "¥${String.format("%.2f", cartItem.price)}"
                quantityTextView.text = cartItem.quantity.toString()

                // 加载商品图片
                Glide.with(productImageView)
                    .load(cartItem.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(productImageView)

                // 事件监听
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    onItemCheckChanged(cartItem, isChecked)
                }

                increaseButton.setOnClickListener {
                    onQuantityChanged(cartItem, cartItem.quantity + 1)
                }

                decreaseButton.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        onQuantityChanged(cartItem, cartItem.quantity - 1)
                    }
                }

                deleteButton.setOnClickListener {
                    onItemDeleted(cartItem)
                }
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
