package com.devmello.gymlog.di

import androidx.room.Room
import com.devmello.gymlog.data.AppDataBase
import com.devmello.gymlog.data.DATABASE_NAME
import com.devmello.gymlog.data.cloud_db.CloudDB
import com.devmello.gymlog.data.cloud_db.MockedCloudDB
import com.devmello.gymlog.data.datastore.UserStore
import com.devmello.gymlog.data.firebase.FireStoreClient
import com.devmello.gymlog.data.firebase.FirebaseUserClient
import com.devmello.gymlog.data.firebase.StorageClient
import com.devmello.gymlog.navigation.viewmodel.MainViewModelImpl
import com.devmello.gymlog.repository.BmiInfoRepositoryImpl
import com.devmello.gymlog.repository.TrainingRepositoryImpl
import com.devmello.gymlog.repository.UserRepositoryImpl
import com.devmello.gymlog.ui.auth.authclient.AuthUiClient
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModel
import com.devmello.gymlog.ui.bmi.viewmodel.BmiCalculatorViewModel
import com.devmello.gymlog.ui.bmi.viewmodel.BmiHistoricViewModelImpl
import com.devmello.gymlog.ui.form.viewmodel.TrainingFormViewModel
import com.devmello.gymlog.ui.home.viewmodel.HomeViewModelImpl
import com.devmello.gymlog.ui.log.viewmodel.TrainingLogViewModelImpl
import com.devmello.gymlog.ui.stopwatch.viewmodel.StopwatchViewModelImpl
import com.devmello.gymlog.ui.user.viewmodel.UserProfileViewModelImpl
import org.koin.android.ext.koin.androidApplication
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
    single<CloudDB> {
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

val mainModule = module {
    single {
        UserStore(androidApplication())
    }
    viewModel() {
        MainViewModelImpl(get(), get())
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
        TrainingLogViewModelImpl(get<TrainingRepositoryImpl>())
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
    single {
        AuthUiClient(androidContext())
    }
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

val stopwatchModule = module {
    viewModel {
        StopwatchViewModelImpl()
    }
}