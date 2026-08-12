package com.example.data.repository

import com.example.data.local.QuizResultEntity
import com.example.data.local.SeedData
import com.example.data.local.VocabDao
import com.example.data.local.VocabEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VocabRepository(private val vocabDao: VocabDao) {

    val allVocab: Flow<List<VocabEntity>> = vocabDao.getAllVocab()
    val favoriteVocab: Flow<List<VocabEntity>> = vocabDao.getFavoriteVocab()
    val wrongVocab: Flow<List<VocabEntity>> = vocabDao.getWrongVocab()
    val vocabCount: Flow<Int> = vocabDao.getVocabCount()
    val favoriteCount: Flow<Int> = vocabDao.getFavoriteCount()
    val wrongWordsCount: Flow<Int> = vocabDao.getWrongWordsCount()
    val quizResults: Flow<List<QuizResultEntity>> = vocabDao.getAllQuizResults()

    suspend fun checkAndSeedDatabase() {
        val currentCount = vocabDao.getVocabCount().first()
        if (currentCount == 0) {
            vocabDao.insertAll(SeedData.initialWords)
        }
    }

    fun getVocabByCategory(category: String): Flow<List<VocabEntity>> {
        return vocabDao.getVocabByCategory(category)
    }

    suspend fun addVocab(
        russianWord: String,
        bengaliMeaning: String,
        category: String
    ): Long {
        val item = VocabEntity(
            russianWord = russianWord.trim(),
            bengaliMeaning = bengaliMeaning.trim(),
            category = category.trim().ifEmpty { "General" }
        )
        return vocabDao.insertVocab(item)
    }

    suspend fun updateVocab(vocab: VocabEntity) {
        vocabDao.updateVocab(vocab)
    }

    suspend fun toggleFavorite(vocab: VocabEntity) {
        vocabDao.updateVocab(vocab.copy(isFavorite = !vocab.isFavorite))
    }

    suspend fun deleteVocab(vocab: VocabEntity) {
        vocabDao.deleteVocab(vocab)
    }

    suspend fun deleteVocabById(id: Long) {
        vocabDao.deleteVocabById(id)
    }

    suspend fun recordQuizAnswer(vocab: VocabEntity, isCorrect: Boolean) {
        if (isCorrect) {
            val updated = vocab.copy(
                correctCount = vocab.correctCount + 1
            )
            vocabDao.updateVocab(updated)
        } else {
            val updated = vocab.copy(
                isWrongWord = true,
                wrongCount = vocab.wrongCount + 1
            )
            vocabDao.updateVocab(updated)
        }
    }

    suspend fun removeFromWrongList(vocab: VocabEntity) {
        vocabDao.updateVocab(vocab.copy(isWrongWord = false))
    }

    suspend fun saveQuizResult(
        totalQuestions: Int,
        correctAnswers: Int,
        wrongAnswers: Int,
        accuracyPercentage: Float,
        quizMode: String
    ): Long {
        val result = QuizResultEntity(
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            wrongAnswers = wrongAnswers,
            accuracyPercentage = accuracyPercentage,
            quizMode = quizMode
        )
        return vocabDao.insertQuizResult(result)
    }
}
