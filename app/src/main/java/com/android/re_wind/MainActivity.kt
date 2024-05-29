package com.android.re_wind

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.re_wind.ui.auth.AuthMainFragment
import com.android.re_wind.ui.home.HomeFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 로그인 상태에 따라 화면 전환
        lifecycleScope.launch {
            viewModel.user.collectLatest {
                // 로그인 O
                if (it == null)  {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AuthMainFragment(), "auth_main")
                        .commit()
                }
                // 로그인 X
                else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment(), "main")
                        .commit()
                }
            }
        }
    }
}