package com.example.portfoliomadeeasy.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _currentUser = mutableStateOf(auth.currentUser)
    private val _isLoading = mutableStateOf(false)
    private val _errorMessage = mutableStateOf("")

    val currentUser: State<FirebaseUser?> = _currentUser

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
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
                    val exception = task.exception
                    val message = when (exception) {
                        is FirebaseAuthWeakPasswordException -> "Lozinka je preslaba"
                        is FirebaseAuthUserCollisionException -> "Oval email se već koristi"
                        is FirebaseAuthInvalidCredentialsException -> "Pogrešan email format"
                        else -> exception?.localizedMessage ?: "Došlo je do pogreške"
                    }
                    onError(message)
                }
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _isLoading.value = true
        _errorMessage.value = ""
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    onSuccess()
                } else {
                    val exception = task.exception
                    val message = when (exception) {
                        is FirebaseAuthInvalidUserException -> "Korisnik ne postoji"
                        is FirebaseAuthInvalidCredentialsException -> "Pogrešna lozinka ili email"
                        else -> "Prijava nije uspjela. Pokušajte ponovno"
                    }
                    onError(message)
                }
            }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}