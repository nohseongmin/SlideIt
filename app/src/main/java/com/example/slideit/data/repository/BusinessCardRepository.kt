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
}
