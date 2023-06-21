package com.example.gymlog.ui.bmi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.repository.BmiInfoRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class BmiCalculatorViewModel(private val repository: BmiInfoRepository) : ViewModel() {

    private val currentUser = Firebase.auth.currentUser

    suspend fun save(bmiInfo: BmiInfo) {
        currentUser?.let {
            repository.save(bmiInfo.copy(userId = it.uid))
        }
    }


}