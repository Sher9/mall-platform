package com.example.mall

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mall.base.router.RouterPath
import com.example.mall.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主 Activity - 底部导航栏容器
 * 使用 ViewBinding + Hilt 依赖注入
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        setupObservers()
        
        // 默认加载首页
        loadHomeFragment()
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadHomeFragment()
                    true
                }
                R.id.nav_category -> {
                    loadCategoryFragment()
                    true
                }
                R.id.nav_cart -> {
                    loadCartFragment()
                    true
                }
                R.id.nav_user -> {
                    loadUserFragment()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupObservers() {
        viewModel.userInfo.observe(this) { userInfo ->
            // 更新用户相关 UI
        }
    }
    
    private fun loadHomeFragment() {
        // 使用 ARouter 跳转到首页模块
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment.newInstance())
            .commit()
    }
    
    private fun loadCategoryFragment() {
        // 使用 ARouter 跳转到分类模块
    }
    
    private fun loadCartFragment() {
        // 使用 ARouter 跳转到购物车模块
    }
    
    private fun loadUserFragment() {
        // 使用 ARouter 跳转到用户模块
    }
}

/**
 * MainViewModel - 主页面业务逻辑
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> = _userInfo
    
    fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo()
                .catch { e ->
                    // 处理错误
                }
                .collect { result ->
                    if (result.code == 200) {
                        _userInfo.postValue(result.data)
                    }
                }
        }
    }
}
