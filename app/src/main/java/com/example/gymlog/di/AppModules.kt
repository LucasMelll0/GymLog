package com.example.gymlog.di

import androidx.room.Room
import com.example.gymlog.database.AppDataBase
import com.example.gymlog.database.DATABASE_NAME
import com.example.gymlog.database.dao.TrainingDao
import com.example.gymlog.repository.TrainingRepositoryImpl
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
}
val repositoryModule = module {
    single {
        TrainingRepositoryImpl(get<TrainingDao>())
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