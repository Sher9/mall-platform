package com.example.mall.product

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.mall.base.router.RouterPath
import com.example.mall.product.ui.components.ProductImageGallery
import com.example.mall.product.ui.components.ProductInfoSection
import com.example.mall.product.ui.components.ProductPurchaseSection
import com.example.mall.product.viewmodel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 商品详情页 - 使用 Jetpack Compose
 * 路由路径：/product/detail
 */
@Route(path = RouterPath.Product.DETAIL)
@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    @Autowired
    @JvmField
    var productId: Long = 0L

    private val viewModel: ProductDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ARouter 注入参数
        ARouter.getInstance().inject(this)

        // 加载商品详情
        viewModel.loadProductDetail(productId)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    ProductDetailScreen(
                        viewModel = viewModel,
                        onBackClick = { finish() },
                        onAddToCart = { quantity ->
                            viewModel.addToCart(quantity)
                        },
                        onBuyNow = {
                            // 立即购买，跳转到确认订单页
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBackClick: () -> Unit,
    onAddToCart: (Int) -> Unit,
    onBuyNow: () -> Unit
) {
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = product?.productName ?: "商品详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        bottomBar = {
            ProductPurchaseSection(
                onAddToCart = { onAddToCart(1) },
                onBuyNow = onBuyNow
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                // 商品图片轮播
                ProductImageGallery(
                    images = listOf(product?.imageUrl ?: "")
                )

                // 商品信息
                ProductInfoSection(
                    product = product,
                    modifier = Modifier.padding(16.dp)
                )

                // 商品描述
                product?.description?.let { description ->
                    Text(
                        text = description,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
