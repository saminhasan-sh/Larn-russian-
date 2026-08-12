package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.QuizResultEntity
import com.example.data.local.VocabEntity
import com.example.data.repository.VocabRepository
import com.example.data.user.GoogleUserProfile
import com.example.data.user.UserAccountManager
import com.example.util.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class QuizMode {
    RU_TO_BN, // Russian -> Bengali
    BN_TO_RU  // Bengali -> Russian
}

data class QuizSessionState(
    val isActive: Boolean = false,
    val mode: QuizMode = QuizMode.RU_TO_BN,
    val questions: List<VocabEntity> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswer: String = "",
    val isAnswerChecked: Boolean = false,
    val isCorrect: Boolean? = null,
    val correctAnswersCount: Int = 0,
    val wrongAnswersCount: Int = 0,
    val isFinished: Boolean = false,
    val wrongWordsInThisSession: List<VocabEntity> = emptyList()
)

data class AppStatistics(
    val totalWords: Int = 0,
    val favoriteWords: Int = 0,
    val wrongWordsCount: Int = 0,
    val totalQuizAttempts: Int = 0,
    val totalQuizCorrect: Int = 0,
    val totalQuizWrong: Int = 0,
    val overallAccuracy: Float = 0f,
    val mostMissedWords: List<VocabEntity> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = VocabRepository(db.vocabDao())
    val accountManager = UserAccountManager(application)
    val ttsManager = TtsManager(application)

    val userProfile: StateFlow<GoogleUserProfile> = accountManager.userProfileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoogleUserProfile()
    )

    val allVocab: StateFlow<List<VocabEntity>> = repository.allVocab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteVocab: StateFlow<List<VocabEntity>> = repository.favoriteVocab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val wrongVocab: StateFlow<List<VocabEntity>> = repository.wrongVocab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quizHistory: StateFlow<List<QuizResultEntity>> = repository.quizResults.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Search & Filter state
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>("All")
    val isFavoriteFilterOnly = MutableStateFlow(false)

    val filteredVocab: StateFlow<List<VocabEntity>> = combine(
        allVocab,
        searchQuery,
        selectedCategoryFilter,
        isFavoriteFilterOnly
    ) { vocabList, query, catFilter, favOnly ->
        vocabList.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.russianWord.contains(query, ignoreCase = true) ||
                    item.bengaliMeaning.contains(query, ignoreCase = true)

            val matchesCat = catFilter == null || catFilter == "All" || item.category.equals(catFilter, ignoreCase = true)
            val matchesFav = !favOnly || item.isFavorite

            matchesQuery && matchesCat && matchesFav
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val defaultCategories = listOf("Nouns", "Verbs", "Adjectives", "Food", "Animals", "Travel", "Family", "Greetings")

    // Quiz Session State
    private val _quizSession = MutableStateFlow(QuizSessionState())
    val quizSession: StateFlow<QuizSessionState> = _quizSession.asStateFlow()

    val quizQuestionCountInput = MutableStateFlow("10")
    val selectedQuizMode = MutableStateFlow(QuizMode.RU_TO_BN)

    // Overall Statistics
    val appStatistics: StateFlow<AppStatistics> = combine(
        allVocab,
        favoriteVocab,
        wrongVocab,
        quizHistory
    ) { all, favs, wrongs, history ->
        val totalAttempts = history.size
        val totalCorrect = history.sumOf { it.correctAnswers }
        val totalWrong = history.sumOf { it.wrongAnswers }
        val grandTotalQuestions = history.sumOf { it.totalQuestions }
        val accuracy = if (grandTotalQuestions > 0) {
            (totalCorrect.toFloat() / grandTotalQuestions.toFloat()) * 100f
        } else 0f

        val mostMissed = all.filter { it.wrongCount > 0 }.sortedByDescending { it.wrongCount }.take(5)

        AppStatistics(
            totalWords = all.size,
            favoriteWords = favs.size,
            wrongWordsCount = wrongs.size,
            totalQuizAttempts = totalAttempts,
            totalQuizCorrect = totalCorrect,
            totalQuizWrong = totalWrong,
            overallAccuracy = accuracy,
            mostMissedWords = mostMissed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppStatistics()
    )

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    fun speakRussian(text: String) {
        ttsManager.speak(text)
    }

    fun addVocab(russian: String, bengali: String, category: String) {
        if (russian.isBlank() || bengali.isBlank()) return
        viewModelScope.launch {
            repository.addVocab(russian, bengali, category)
        }
    }

    fun updateVocab(vocab: VocabEntity) {
        viewModelScope.launch {
            repository.updateVocab(vocab)
        }
    }

    fun toggleFavorite(vocab: VocabEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(vocab)
        }
    }

    fun deleteVocab(vocab: VocabEntity) {
        viewModelScope.launch {
            repository.deleteVocab(vocab)
        }
    }

    fun removeFromWrongList(vocab: VocabEntity) {
        viewModelScope.launch {
            repository.removeFromWrongList(vocab)
        }
    }

    fun googleSignIn(email: String, displayName: String) {
        viewModelScope.launch {
            accountManager.signInWithGoogle(email, displayName)
        }
    }

    fun googleSignOut() {
        viewModelScope.launch {
            accountManager.signOut()
        }
    }

    // Quiz functions
    fun startQuiz(useOnlyWrongWords: Boolean = false) {
        viewModelScope.launch {
            val sourceList = if (useOnlyWrongWords) {
                wrongVocab.value.ifEmpty { allVocab.value }
            } else {
                allVocab.value
            }

            if (sourceList.isEmpty()) return@launch

            val requestedCount = quizQuestionCountInput.value.toIntOrNull() ?: 10
            val shuffled = sourceList.shuffled()
            val questions = shuffled.take(requestedCount.coerceAtMost(shuffled.size))

            _quizSession.value = QuizSessionState(
                isActive = true,
                mode = selectedQuizMode.value,
                questions = questions,
                currentIndex = 0,
                userAnswer = "",
                isAnswerChecked = false,
                isCorrect = null,
                correctAnswersCount = 0,
                wrongAnswersCount = 0,
                isFinished = false
            )
        }
    }

    fun updateQuizUserAnswer(answer: String) {
        _quizSession.value = _quizSession.value.copy(userAnswer = answer)
    }

    fun checkQuizAnswer() {
        val session = _quizSession.value
        if (!session.isActive || session.questions.isEmpty() || session.isAnswerChecked) return

        val currentQuestion = session.questions[session.currentIndex]
        val expected = when (session.mode) {
            QuizMode.RU_TO_BN -> currentQuestion.bengaliMeaning
            QuizMode.BN_TO_RU -> currentQuestion.russianWord
        }

        val userText = session.userAnswer.trim().lowercase()
        val expectedText = expected.trim().lowercase()

        // Flexible fuzzy or substring matching for Bengali/Russian answers
        val isMatch = userText.isNotEmpty() && (
                expectedText.contains(userText) ||
                userText.contains(expectedText) ||
                cleanAnswerString(userText) == cleanAnswerString(expectedText)
        )

        viewModelScope.launch {
            repository.recordQuizAnswer(currentQuestion, isMatch)
        }

        val updatedWrongWords = if (!isMatch) {
            session.wrongWordsInThisSession + currentQuestion
        } else {
            session.wrongWordsInThisSession
        }

        _quizSession.value = session.copy(
            isAnswerChecked = true,
            isCorrect = isMatch,
            correctAnswersCount = if (isMatch) session.correctAnswersCount + 1 else session.correctAnswersCount,
            wrongAnswersCount = if (!isMatch) session.wrongAnswersCount + 1 else session.wrongAnswersCount,
            wrongWordsInThisSession = updatedWrongWords
        )
    }

    fun nextQuizQuestion() {
        val session = _quizSession.value
        if (!session.isAnswerChecked) return

        val nextIndex = session.currentIndex + 1
        if (nextIndex >= session.questions.size) {
            // Finish Quiz
            val total = session.questions.size
            val correct = session.correctAnswersCount
            val wrong = session.wrongAnswersCount
            val accuracy = if (total > 0) (correct.toFloat() / total.toFloat()) * 100f else 0f

            viewModelScope.launch {
                repository.saveQuizResult(
                    totalQuestions = total,
                    correctAnswers = correct,
                    wrongAnswers = wrong,
                    accuracyPercentage = accuracy,
                    quizMode = session.mode.name
                )
            }

            _quizSession.value = session.copy(
                isFinished = true,
                isAnswerChecked = false,
                userAnswer = ""
            )
        } else {
            _quizSession.value = session.copy(
                currentIndex = nextIndex,
                userAnswer = "",
                isAnswerChecked = false,
                isCorrect = null
            )
        }
    }

    fun resetQuiz() {
        _quizSession.value = QuizSessionState()
    }

    private fun cleanAnswerString(text: String): String {
        return text.replace(Regex("[^\\p{L}\\p{Nd}]"), "").lowercase()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
