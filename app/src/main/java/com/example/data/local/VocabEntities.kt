package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocab_items")
data class VocabEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val russianWord: String,
    val bengaliMeaning: String,
    val category: String, // Nouns, Verbs, Adjectives, Food, Animals, Travel, Family, or Custom
    val isFavorite: Boolean = false,
    val isWrongWord: Boolean = false,
    val wrongCount: Int = 0,
    val correctCount: Int = 0,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_history")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val accuracyPercentage: Float,
    val quizMode: String, // "RU_TO_BN" or "BN_TO_RU"
    val timestamp: Long = System.currentTimeMillis()
)
