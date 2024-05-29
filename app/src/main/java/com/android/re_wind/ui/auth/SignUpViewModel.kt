package com.android.re_wind.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.re_wind.data.repositories.AuthRepository

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy { AuthRepository.getInstance() }

    suspend fun signUp(nick: String, email: String, password: String) =
        repository.signUp(nick, email, password)
}