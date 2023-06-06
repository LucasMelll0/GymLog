package com.example.gymlog.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymlog.database.converters.Converters
import com.example.gymlog.database.dao.BmiInfoDao
import com.example.gymlog.database.dao.TrainingDao
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.Exercise
import com.example.gymlog.model.Training

const val DATABASE_NAME = "GymLog Database"
@Database(
    version = 3,
    entities = [Training::class, Exercise::class, BmiInfo::class],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    abstract fun bmiInfoDao(): BmiInfoDao
}