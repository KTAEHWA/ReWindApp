// Firebase 연동 코드

package com.android.re_wind

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.re_wind.data.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application),
    // DB 정보 받아오기
    FirebaseAuth.AuthStateListener {
    private val auth = Firebase.auth

    init {
        auth.addAuthStateListener(this)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(this)
        super.onCleared()
    }

    private val authRepository by lazy { AuthRepository.getInstance() }

    // MainActivity에서 참조하여 로그인 여부 확인
    var user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(auth.currentUser)

    // 로그인 상태 변경 -> user 값 변경
    override fun onAuthStateChanged(p0: FirebaseAuth) {
        user.value = p0.currentUser
    }
}