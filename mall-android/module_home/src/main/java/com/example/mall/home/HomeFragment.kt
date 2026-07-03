package com.example.mall.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.mall.base.router.RouterPath
import com.example.mall.home.viewmodel.HomeViewModel
import com.example.mall.home.ui.components.BannerComponent
import com.example.mall.home.ui.components.ProductGrid
import dagger.hilt.android.AndroidEntryPoint

/**
 * 首页 Fragment - 使用 Jetpack Compose
 * 路由路径：/home/main
 */
@Route(path = RouterPath.Home.HOME)
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HomeScreen(
                    viewModel = viewModel,
                    onProductClick = { productId ->
                        // 使用 ARouter 跳转到商品详情
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProductClick: (Long) -> Unit
) {
    val hotProducts by viewModel.hotProducts.collectAsState()
    val newProducts by viewModel.newProducts.collectAsState()
    val banners = remember {
        listOf(
            BannerItem("夏季大促销", "精选商品低至5折", 0xFF667eea),
            BannerItem("新品首发", "最新科技产品，抢先体验", 0xFFf093fb),
            BannerItem("品质生活", "精选家居好物", 0xFF4facfe)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 轮播图
        item {
            BannerComponent(banners = banners)
        }

        // 热门推荐
        item {
            SectionHeader(title = "热门推荐")
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hotProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.productId) }
                    )
                }
            }
        }

        // 新品上市
        item {
            SectionHeader(title = "新品上市")
        }
        item {
            ProductGrid(
                products = newProducts,
                onProductClick = onProductClick
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            text = "查看更多",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

data class BannerItem(
    val title: String,
    val subtitle: String,
    val bgColor: Long
)
