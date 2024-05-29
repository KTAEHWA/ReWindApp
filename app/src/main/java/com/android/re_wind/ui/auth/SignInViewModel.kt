package com.android.re_wind.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.re_wind.data.repositories.AuthRepository

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy { AuthRepository.getInstance() }

    suspend fun signIn(email: String, password: String) = repository.signIn(email, password)
}