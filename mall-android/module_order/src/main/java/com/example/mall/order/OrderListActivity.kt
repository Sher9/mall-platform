package com.example.mall.order

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.mall.base.router.RouterPath
import com.example.mall.order.adapter.OrderAdapter
import com.example.mall.order.databinding.ActivityOrderListBinding
import com.example.mall.order.viewmodel.OrderListViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 订单列表页面
 * 路由路径：/order/list
 */
@Route(path = RouterPath.Order.LIST)
@AndroidEntryPoint
class OrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private val viewModel: OrderListViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // 加载订单数据
        viewModel.loadOrders()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onPayClick = { order -> viewModel.payOrder(order.orderId) },
            onCancelClick = { order -> viewModel.cancelOrder(order.orderId) },
            onReceiveClick = { order -> viewModel.receiveOrder(order.orderId) },
            onItemClick = { order ->
                // 跳转到订单详情
            }
        )

        binding.orderRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderListActivity)
            adapter = orderAdapter
        }
    }

    private fun setupObservers() {
        // 观察订单列表
        viewModel.orders.observe(this) { orders ->
            orderAdapter.submitList(orders)
            binding.emptyTextView.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        }

        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        // 观察操作结果
        viewModel.operationResult.observe(this) { result ->
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            viewModel.loadOrders() // 刷新列表
        }
    }

    private fun setupClickListeners() {
        // 下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadOrders()
        }

        // 订单状态筛选
        binding.statusTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val status = when (tab?.position) {
                    1 -> 0 // 待付款
                    2 -> 1 // 待发货
                    3 -> 2 // 待收货
                    4 -> 3 // 已完成
                    else -> null // 全部
                }
                viewModel.filterOrdersByStatus(status)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}

/**
 * 订单列表 ViewModel
 */
@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableLiveData<List<OrderInfo>>(emptyList())
    val orders: LiveData<List<OrderInfo>> = _orders

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _operationResult = MutableLiveData<String>()
    val operationResult: LiveData<String> = _operationResult

    fun loadOrders() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            orderRepository.getOrderList()
                .catch { e ->
                    _operationResult.postValue("加载失败: ${e.message}")
                    _isLoading.postValue(false)
                }
                .collect { result ->
                    if (result.code == 200) {
                        _orders.postValue(result.data?.records ?: emptyList())
                    } else {
                        _operationResult.postValue(result.message)
                    }
                    _isLoading.postValue(false)
                }
        }
    }

    fun filterOrdersByStatus(status: Int?) {
        viewModelScope.launch {
            if (status == null) {
                loadOrders()
            } else {
                // 本地筛选
                val filtered = _orders.value?.filter { it.status == status }
                _orders.postValue(filtered ?: emptyList())
            }
        }
    }

    fun payOrder(orderId: Long) {
        viewModelScope.launch {
            orderRepository.payOrder(orderId)
                .catch { e ->
                    _operationResult.postValue("支付失败: ${e.message}")
                }
                .collect { result ->
                    if (result.code == 200) {
                        _operationResult.postValue("支付成功")
                    } else {
                        _operationResult.postValue(result.message)
                    }
                }
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId)
                .catch { e ->
                    _operationResult.postValue("取消失败: ${e.message}")
                }
                .collect { result ->
                    if (result.code == 200) {
                        _operationResult.postValue("订单已取消")
                    } else {
                        _operationResult.postValue(result.message)
                    }
                }
        }
    }

    fun receiveOrder(orderId: Long) {
        viewModelScope.launch {
            orderRepository.receiveOrder(orderId)
                .catch { e ->
                    _operationResult.postValue("确认失败: ${e.message}")
                }
                .collect { result ->
                    if (result.code == 200) {
                        _operationResult.postValue("确认收货成功")
                    } else {
                        _operationResult.postValue(result.message)
                    }
                }
        }
    }
}
