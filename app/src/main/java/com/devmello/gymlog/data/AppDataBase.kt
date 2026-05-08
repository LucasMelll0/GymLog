package com.devmello.gymlog.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devmello.gymlog.data.converters.Converters
import com.devmello.gymlog.data.dao.BmiInfoDao
import com.devmello.gymlog.data.dao.TrainingDao
import com.devmello.gymlog.data.dao.UserDao
import com.devmello.gymlog.model.BmiInfo
import com.devmello.gymlog.model.Exercise
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.model.User

const val DATABASE_NAME = "GymLog Database"
@Database(
    version = 1,
    entities = [Training::class, Exercise::class, BmiInfo::class, User::class],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    abstract fun bmiInfoDao(): BmiInfoDao

    abstract fun userDao(): UserDao
}