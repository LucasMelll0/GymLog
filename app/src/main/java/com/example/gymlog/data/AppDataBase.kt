package com.example.gymlog.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymlog.data.converters.Converters
import com.example.gymlog.data.dao.BmiInfoDao
import com.example.gymlog.data.dao.TrainingDao
import com.example.gymlog.data.dao.UserDao
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.Exercise
import com.example.gymlog.model.Training
import com.example.gymlog.model.User

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