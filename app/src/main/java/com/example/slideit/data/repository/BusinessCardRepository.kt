package com.example.slideit.data.repository

import com.example.slideit.data.dao.BusinessCardDao
import com.example.slideit.data.model.BusinessCard
import kotlinx.coroutines.flow.Flow

/**
 * 명함 데이터 저장소
 */
class BusinessCardRepository(private val dao: BusinessCardDao) {

    /**
     * 모든 명함 조회
     */
    fun getAllCards(): Flow<List<BusinessCard>> = dao.getAllCards()

    /**
     * 내 명함만 조회
     */
    fun getMyCards(): Flow<List<BusinessCard>> = dao.getMyCards()

    /**
     * 받은 명함만 조회
     */
    fun getReceivedCards(): Flow<List<BusinessCard>> = dao.getReceivedCards()

    /**
     * ID로 명함 조회
     */
    suspend fun getCardById(cardId: String): BusinessCard? = dao.getCardById(cardId)

    /**
     * 내 명함 중 첫 번째 조회 (공유용)
     */
    fun getFirstMyCard(): Flow<BusinessCard?> = dao.getFirstMyCard()

    /**
     * 명함 검색
     */
    fun searchCards(query: String): Flow<List<BusinessCard>> = dao.searchCards(query)

    /**
     * 명함 추가
     */
    suspend fun insertCard(card: BusinessCard) = dao.insertCard(card)

    /**
     * 여러 명함 추가
     */
    suspend fun insertCards(cards: List<BusinessCard>) = dao.insertCards(cards)

    /**
     * 다른 '내 명함'을 모두 해제
     */
    suspend fun unselectOtherMyCards(currentMyCardId: String) = dao.unselectOtherMyCards(currentMyCardId)

    /**
     * 명함 수정
     */
    suspend fun updateCard(card: BusinessCard) = dao.updateCard(card)

    /**
     * 명함 삭제
     */
    suspend fun deleteCard(card: BusinessCard) = dao.deleteCard(card)

    /**
     * ID로 명함 삭제
     */
    suspend fun deleteCardById(cardId: String) = dao.deleteCardById(cardId)

    /**
     * 모든 명함 삭제
     */
    suspend fun deleteAllCards() = dao.deleteAllCards()

    /**
     * 즐겨찾기 명함만 조회
     */
    fun getFavoriteCards(): Flow<List<BusinessCard>> = dao.getFavoriteCards()

    /**
     * 카테고리별 명함 조회
     */
    fun getCardsByCategory(category: String): Flow<List<BusinessCard>> = dao.getCardsByCategory(category)

    /**
     * 모든 카테고리 조회
     */
    fun getAllCategories(): Flow<List<String>> = dao.getAllCategories()

    /**
     * 즐겨찾기 토글
     */
    suspend fun toggleFavorite(cardId: String, isFavorite: Boolean) = dao.toggleFavorite(cardId, isFavorite)
}
