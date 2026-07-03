package com.example.mall.user

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.mall.base.router.RouterPath
import com.example.mall.user.databinding.ActivityLoginBinding
import com.example.mall.user.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 登录页面
 * 使用 ARouter 路由，路径：/user/login
 */
@Route(path = RouterPath.User.LOGIN)
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        // 观察登录结果
        viewModel.loginResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            binding.loginButton.isEnabled = !isLoading
            // 可以显示加载中...
        }
    }

    private fun setupClickListeners() {
        // 登录按钮
        binding.loginButton.setOnClickListener {
            val phone = binding.phoneEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(phone, password)) {
                viewModel.login(phone, password)
            }
        }

        // 注册链接
        binding.registerTextView.setOnClickListener {
            // 使用 ARouter 跳转到注册页面
        }
    }

    private fun validateInput(phone: String, password: String): Boolean {
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}

/**
 * LoginViewModel - 登录业务逻辑
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(phone: String, password: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            userRepository.login(phone, password)
                .catch { e ->
                    _loginResult.postValue(LoginResult(false, e.message ?: "登录失败"))
                    _isLoading.postValue(false)
                }
                .collect { result ->
                    if (result.code == 200) {
                        // 保存 token
                        val token = result.data?.token ?: ""
                        // 保存 token 到 MMKV
                        MMKV.defaultMMKV()?.putString("token", token)
                        _loginResult.postValue(LoginResult(true, "登录成功"))
                    } else {
                        _loginResult.postValue(LoginResult(false, result.message))
                    }
                    _isLoading.postValue(false)
                }
        }
    }
}

/**
 * 登录结果数据类
 */
data class LoginResult(
    val isSuccess: Boolean,
    val errorMessage: String = ""
)
