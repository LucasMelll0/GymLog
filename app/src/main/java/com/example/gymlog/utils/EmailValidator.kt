package com.example.gymlog.utils

import android.util.Patterns

fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()