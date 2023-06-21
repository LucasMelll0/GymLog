package com.example.gymlog.di

import androidx.room.Room
import com.example.gymlog.data.AppDataBase
import com.example.gymlog.data.DATABASE_NAME
import com.example.gymlog.data.firebase.FireStoreClient
import com.example.gymlog.repository.BmiInfoRepositoryImpl
import com.example.gymlog.repository.TrainingRepositoryImpl
import com.example.gymlog.repository.UserRepositoryImpl
import com.example.gymlog.ui.auth.viewmodel.AuthViewModel
import com.example.gymlog.ui.bmi.viewmodel.BmiCalculatorViewModel
import com.example.gymlog.ui.bmi.viewmodel.BmiHistoricViewModel
import com.example.gymlog.ui.form.viewmodel.TrainingFormViewModel
import com.example.gymlog.ui.home.viewmodel.HomeViewModel
import com.example.gymlog.ui.log.viewmodel.TrainingLogViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val roomModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<AppDataBase>().trainingDao()
    }
    single {
        get<AppDataBase>().bmiInfoDao()
    }
    single {
        get<AppDataBase>().userDao()
    }
}

val firebaseModule = module {
    single {
        FireStoreClient()
    }
}

val repositoryModule = module {
    single {
        TrainingRepositoryImpl(get())
    }
    single {
        BmiInfoRepositoryImpl(get(), get())
    }
    single {
        UserRepositoryImpl(get(), get())
    }
}

val homeModule = module {
    viewModel {
        HomeViewModel(get<TrainingRepositoryImpl>())
    }
}

val formModule = module {
    viewModel {
        TrainingFormViewModel(get<TrainingRepositoryImpl>())
    }
}

val logModule = module {
    viewModel {
        TrainingLogViewModel(get<TrainingRepositoryImpl>())
    }
}

val bmiModule = module {
    viewModel {
        BmiCalculatorViewModel(get<BmiInfoRepositoryImpl>())
    }
    viewModel {
        BmiHistoricViewModel(
            userRepository = get<UserRepositoryImpl>(),
            bmiRepository = get<BmiInfoRepositoryImpl>()
        )
    }
}

val authModule = module {
    viewModel {
        AuthViewModel()
    }
}