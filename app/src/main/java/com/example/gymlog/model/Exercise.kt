package com.example.gymlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Exercise(
    @PrimaryKey
    val exerciseId: String = UUID.randomUUID().toString(),
    val title: String,
    val repetitions: Int,
    val series: Int,
    val isChecked: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Exercise) {
            val otherTitle = other.title
            val otherSeries = other.series
            val otherRepetitions = other.repetitions
            return otherTitle == this.title &&
                    otherSeries == this.series &&
                    otherRepetitions == this.repetitions
        } else {
            false
        }

    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + repetitions
        result = 31 * result + series
        result = 31 * result + isChecked.hashCode()
        return result
    }
}