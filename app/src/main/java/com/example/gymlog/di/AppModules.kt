package com.example.gymlog.di

import androidx.room.Room
import com.example.gymlog.data.AppDataBase
import com.example.gymlog.data.DATABASE_NAME
import com.example.gymlog.data.firebase.FireStoreClient
import com.example.gymlog.data.firebase.FirebaseUserClient
import com.example.gymlog.data.firebase.StorageClient
import com.example.gymlog.repository.BmiInfoRepositoryImpl
import com.example.gymlog.repository.TrainingRepositoryImpl
import com.example.gymlog.repository.UserRepositoryImpl
import com.example.gymlog.ui.auth.viewmodel.AuthViewModel
import com.example.gymlog.ui.bmi.viewmodel.BmiCalculatorViewModel
import com.example.gymlog.ui.bmi.viewmodel.BmiHistoricViewModelImpl
import com.example.gymlog.ui.form.viewmodel.TrainingFormViewModel
import com.example.gymlog.ui.home.viewmodel.HomeViewModelImpl
import com.example.gymlog.ui.log.viewmodel.TrainingLogViewModel
import com.example.gymlog.ui.user.viewmodel.UserProfileViewModelImpl
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
    single {
        FirebaseUserClient(get())
    }
    single {
        StorageClient()
    }
}

val repositoryModule = module {
    single {
        TrainingRepositoryImpl(get(), get())
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
        HomeViewModelImpl(get<TrainingRepositoryImpl>())
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
        BmiHistoricViewModelImpl(
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

val userProfileModule = module {
    viewModel {
        UserProfileViewModelImpl(
            userClient = get(),
            trainingRepository = get<TrainingRepositoryImpl>(),
            bmiInfoRepository = get<BmiInfoRepositoryImpl>(),
            userRepository = get<UserRepositoryImpl>()
        )
    }
}