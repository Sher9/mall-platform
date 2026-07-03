package com.example.mall.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mall.base.data.model.Product
import com.example.mall.base.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页 ViewModel
 * 使用 StateFlow 驱动 Compose UI
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _hotProducts = MutableStateFlow<List<Product>>(emptyList())
    val hotProducts: StateFlow<List<Product>> = _hotProducts.asStateFlow()

    private val _newProducts = MutableStateFlow<List<Product>>(emptyList())
    val newProducts: StateFlow<List<Product>> = _newProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true

            // 加载热门商品
            productRepository.getProductList(pageNum = 1, pageSize = 10)
                .collect { result ->
                    if (result.code == 200) {
                        _hotProducts.value = result.data?.records ?: emptyList()
                    }
                }

            // 加载新品
            productRepository.getProductList(pageNum = 1, pageSize = 10)
                .collect { result ->
                    if (result.code == 200) {
                        _newProducts.value = result.data?.records ?: emptyList()
                    }
                }

            _isLoading.value = false
        }
    }
}
