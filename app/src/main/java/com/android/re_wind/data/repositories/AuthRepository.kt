package com.android.re_wind.data.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository private constructor() {
    companion object {
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            if (instance == null) {
                instance = AuthRepository()
            }

            return instance!!
        }
    }

    private val auth by lazy { Firebase.auth }

    /**
     * 로그인 함수
     *
     * @param email 이메일 주소
     * @param password 비밀번호
     */
    suspend fun signIn(email: String, password: String): AuthResult? {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * 회원가입 함수
     *
     * @param nick 닉네임
     * @param email 이메일 주소
     * @param password 비밀번호
     */
    suspend fun signUp(nick: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()

        try {
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(nick)
                    .build()
            )?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}