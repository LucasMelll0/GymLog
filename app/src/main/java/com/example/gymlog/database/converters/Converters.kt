package com.example.gymlog.database.converters

import androidx.room.TypeConverter
import com.example.gymlog.model.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromStringToStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>(){}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringListToString(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun fromExerciseListToString(list: List<Exercise>): String = Gson().toJson(list)

    @TypeConverter
    fun fromStringToExerciseList(value: String): List<Exercise> {
        val listType = object : TypeToken<List<Exercise>>(){}.type
        return Gson().fromJson(value, listType)
    }
}