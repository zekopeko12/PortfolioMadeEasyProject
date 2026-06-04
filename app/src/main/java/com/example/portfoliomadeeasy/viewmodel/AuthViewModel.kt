package com.example.portfoliomadeeasy.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _currentUser = mutableStateOf(auth.currentUser)
    private val _isLoading = mutableStateOf(false)
    private val _errorMessage = mutableStateOf("")

    val currentUser: State<FirebaseUser?> = _currentUser
    val isLoading: State<Boolean> = _isLoading
    val errorMessage: State<String> = _errorMessage


    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        _errorMessage.value = ""
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    onSuccess()
                } else {
                    _errorMessage.value = task.exception?.localizedMessage ?: "Registration failed"
                }
            }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _errorMessage.value = ""
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    onSuccess()
                } else {
                    _errorMessage.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}