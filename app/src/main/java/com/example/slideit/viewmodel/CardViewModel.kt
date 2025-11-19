package com.example.slideit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.slideit.data.database.AppDatabase
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.data.repository.BusinessCardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 명함 관리 ViewModel
 */
class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BusinessCardRepository

    // 모든 명함
    val allCards: Flow<List<BusinessCard>>

    // 내 명함
    val myCards: Flow<List<BusinessCard>>

    // 받은 명함
    val receivedCards: Flow<List<BusinessCard>>

    // 내 명함 (공유용)
    val firstMyCard: Flow<BusinessCard?>

    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 검색 결과
    private val _searchResults = MutableStateFlow<List<BusinessCard>>(emptyList())
    val searchResults: StateFlow<List<BusinessCard>> = _searchResults.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).businessCardDao()
        repository = BusinessCardRepository(dao)
        allCards = repository.getAllCards()
        myCards = repository.getMyCards()
        receivedCards = repository.getReceivedCards()
        firstMyCard = repository.getFirstMyCard()
    }

    /**
     * 명함 추가
     */
    fun insertCard(card: BusinessCard) = viewModelScope.launch {
        repository.insertCard(card)
    }

    /**
     * 여러 명함 추가
     */
    fun insertCards(cards: List<BusinessCard>) = viewModelScope.launch {
        repository.insertCards(cards)
    }

    /**
     * 명함 수정
     */
    fun updateCard(card: BusinessCard) = viewModelScope.launch {
        repository.updateCard(card)
    }

    /**
     * 명함 삭제
     */
    fun deleteCard(card: BusinessCard) = viewModelScope.launch {
        repository.deleteCard(card)
    }

    /**
     * ID로 명함 삭제
     */
    fun deleteCardById(cardId: String) = viewModelScope.launch {
        repository.deleteCardById(cardId)
    }

    /**
     * ID로 명함 조회
     */
    suspend fun getCardById(cardId: String): BusinessCard? {
        return repository.getCardById(cardId)
    }

    /**
     * 명함 검색
     */
    fun searchCards(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        } else {
            viewModelScope.launch {
                repository.searchCards(query).collect { results ->
                    _searchResults.value = results
                }
            }
        }
    }

    /**
     * 샘플 데이터 추가 (테스트용)
     */
    fun addSampleData() = viewModelScope.launch {
        val sampleCards = listOf(
            BusinessCard(
                name = "김철수",
                position = "대표이사",
                department = "경영총괄",
                company = "ABC Corp",
                email = "kim@abc.com",
                phone = "+82-10-1234-5678",
                address = "서울시 강남구 테헤란로 123",
                backgroundColor = 0xFF1E3A8A,
                textColor = 0xFFFFFFFF,
                accentColor = 0xFF60A5FA
            ),
            BusinessCard(
                name = "이영희",
                position = "부장",
                department = "마케팅팀",
                company = "XYZ Ltd",
                email = "lee@xyz.com",
                phone = "+82-10-2345-6789",
                address = "서울시 서초구 서초대로 456",
                backgroundColor = 0xFFEC4899,
                textColor = 0xFFFFFFFF,
                accentColor = 0xFFFCE7F3
            ),
            BusinessCard(
                name = "박민수",
                position = "과장",
                department = "개발팀",
                company = "DEF Inc",
                email = "park@def.com",
                phone = "+82-10-3456-7890",
                address = "서울시 성동구 왕십리로 789",
                backgroundColor = 0xFF0F172A,
                textColor = 0xFFFFFFFF,
                accentColor = 0xFF38BDF8
            )
        )
        repository.insertCards(sampleCards)
    }
}
