package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocab_items ORDER BY addedTimestamp DESC")
    fun getAllVocab(): Flow<List<VocabEntity>>

    @Query("SELECT * FROM vocab_items WHERE isFavorite = 1 ORDER BY addedTimestamp DESC")
    fun getFavoriteVocab(): Flow<List<VocabEntity>>

    @Query("SELECT * FROM vocab_items WHERE isWrongWord = 1 ORDER BY wrongCount DESC")
    fun getWrongVocab(): Flow<List<VocabEntity>>

    @Query("SELECT * FROM vocab_items WHERE category = :category ORDER BY addedTimestamp DESC")
    fun getVocabByCategory(category: String): Flow<List<VocabEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocab(vocab: VocabEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<VocabEntity>)

    @Update
    suspend fun updateVocab(vocab: VocabEntity)

    @Delete
    suspend fun deleteVocab(vocab: VocabEntity)

    @Query("DELETE FROM vocab_items WHERE id = :id")
    suspend fun deleteVocabById(id: Long)

    @Query("SELECT COUNT(*) FROM vocab_items")
    fun getVocabCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocab_items WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocab_items WHERE isWrongWord = 1")
    fun getWrongWordsCount(): Flow<Int>

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getAllQuizResults(): Flow<List<QuizResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResultEntity): Long

    @Query("DELETE FROM vocab_items")
    suspend fun deleteAllVocab()
}
