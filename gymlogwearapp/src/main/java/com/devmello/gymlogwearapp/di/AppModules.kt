package com.devmello.gymlogwearapp.di

import com.devmello.gymlogwearapp.domain.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel {
        MainViewModel(get())
    }
}