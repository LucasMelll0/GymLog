package com.example.gymlog.ui.bmi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.repository.BmiInfoRepository

class BmiCalculatorViewModel(private val repository: BmiInfoRepository) : ViewModel() {

    suspend fun save(bmiInfo: BmiInfo) = repository.save(bmiInfo)


}