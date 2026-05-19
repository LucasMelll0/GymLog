package com.devmello.gymlog.di

import androidx.room.Room
import com.devmello.gymlog.core.model.repositories.AccountRepository
import com.devmello.gymlog.core.model.repositories.AuthRepository
import com.devmello.gymlog.core.model.repositories.UserPreferencesRepository
import com.devmello.gymlog.core.model.repositories.UserRepository
import com.devmello.gymlog.core.navigation.NavigationManager
import com.devmello.gymlog.core.ui.LoadingManager
import com.devmello.gymlog.core.ui.MessageManager
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.data.AppDataBase
import com.devmello.gymlog.data.DATABASE_NAME
import com.devmello.gymlog.data.cloud_db.CloudDB
import com.devmello.gymlog.data.datastore.UserStore
import com.devmello.gymlog.data.firebase.FireStoreClient
import com.devmello.gymlog.data.firebase.FirebaseAuthRepository
import com.devmello.gymlog.data.firebase.FirebaseUserClient
import com.devmello.gymlog.data.firebase.StorageClient
import com.devmello.gymlog.navigation.viewmodel.MainViewModelImpl
import com.devmello.gymlog.repository.BmiInfoRepository
import com.devmello.gymlog.repository.BmiInfoRepositoryImpl
import com.devmello.gymlog.repository.TrainingRepository
import com.devmello.gymlog.repository.TrainingRepositoryImpl
import com.devmello.gymlog.repository.UserRepositoryImpl
import com.devmello.gymlog.ui.auth.viewmodel.AuthViewModelImpl
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
    single<AccountRepository> {
        FirebaseUserClient(get())
    }
    single {
        StorageClient()
    }
}

val repositoryModule = module {
    single<TrainingRepository> {
        TrainingRepositoryImpl(get(), get())
    }
    single<BmiInfoRepository> {
        BmiInfoRepositoryImpl(get(), get())
    }
    single<UserRepository> {
        UserRepositoryImpl(get(), get())
    }
}

val mainModule = module {
    single<LoadingManager> {
        LoadingManager()
    }
    single<ScaffoldManager> {
        ScaffoldManager()
    }

    single<MessageManager> {
        MessageManager()
    }

    single<NavigationManager> {
        NavigationManager()
    }

    single<UserPreferencesRepository> {
        UserStore(androidApplication())
    }
    viewModel {
        MainViewModelImpl(get())
    }

}

val homeModule = module {
    viewModel {
        HomeViewModelImpl(get())
    }
}

val formModule = module {
    viewModel {
        TrainingFormViewModel(get())
    }
}

val logModule = module {
    viewModel {
        TrainingLogViewModelImpl(get())
    }
}

val bmiModule = module {
    viewModel {
        BmiCalculatorViewModel(get())
    }
    viewModel {
        BmiHistoricViewModelImpl(
            userRepository = get(),
            bmiRepository = get()
        )
    }
}

val authModule = module {
    single<AuthRepository> {
        FirebaseAuthRepository(context = androidContext())
    }
    viewModel {
        AuthViewModelImpl(
            authRepository = get(),
            userPreferencesRepository = get(),
            loadingManager = get(),
            messageManager = get(),
            navigationManager = get()
        )
    }
}

val userProfileModule = module {
    viewModel {
        UserProfileViewModelImpl(
            accountRepository = get(),
            trainingRepository = get(),
            bmiInfoRepository = get(),
            userRepository = get(),
            userPreferencesRepository = get()
        )
    }
}

val stopwatchModule = module {
    viewModel {
        StopwatchViewModelImpl()
    }
}