package com.example.slideit.data.dao

import androidx.room.*
import com.example.slideit.data.model.BusinessCard
import kotlinx.coroutines.flow.Flow

/**
 * 명함 데이터 접근 객체 (DAO)
 */
@Dao
interface BusinessCardDao {
    /**
     * 모든 명함 조회 (Flow로 실시간 업데이트)
     */
    @Query("SELECT * FROM business_cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<BusinessCard>>

    /**
     * 내 명함만 조회
     */
    @Query("SELECT * FROM business_cards WHERE isMyCard = 1 ORDER BY createdAt DESC")
    fun getMyCards(): Flow<List<BusinessCard>>

    /**
     * 받은 명함만 조회
     */
    @Query("SELECT * FROM business_cards WHERE isMyCard = 0 ORDER BY createdAt DESC")
    fun getReceivedCards(): Flow<List<BusinessCard>>

    /**
     * ID로 명함 조회
     */
    @Query("SELECT * FROM business_cards WHERE id = :cardId")
    suspend fun getCardById(cardId: String): BusinessCard?

    /**
     * 내 명함 중 첫 번째 조회 (공유용)
     */
    @Query("SELECT * FROM business_cards WHERE isMyCard = 1 ORDER BY createdAt DESC LIMIT 1")
    fun getFirstMyCard(): Flow<BusinessCard?>

    /**
     * 명함 검색 (이름 또는 회사명)
     */
    @Query("SELECT * FROM business_cards WHERE name LIKE '%' || :query || '%' OR company LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchCards(query: String): Flow<List<BusinessCard>>

    /**
     * 명함 추가
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: BusinessCard)

    /**
     * 여러 명함 추가
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<BusinessCard>)

    /**
     * 명함 수정
     */
    @Update
    suspend fun updateCard(card: BusinessCard)

    /**
     * 명함 삭제
     */
    @Delete
    suspend fun deleteCard(card: BusinessCard)

    /**
     * ID로 명함 삭제
     */
    @Query("DELETE FROM business_cards WHERE id = :cardId")
    suspend fun deleteCardById(cardId: String)

    /**
     * 모든 명함 삭제
     */
    @Query("DELETE FROM business_cards")
    suspend fun deleteAllCards()

    /**
     * 즐겨찾기 명함만 조회
     */
    @Query("SELECT * FROM business_cards WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteCards(): Flow<List<BusinessCard>>

    /**
     * 카테고리별 명함 조회
     */
    @Query("SELECT * FROM business_cards WHERE category = :category ORDER BY createdAt DESC")
    fun getCardsByCategory(category: String): Flow<List<BusinessCard>>

    /**
     * 모든 카테고리 조회
     */
    @Query("SELECT DISTINCT category FROM business_cards WHERE category != '' ORDER BY category")
    fun getAllCategories(): Flow<List<String>>

    /**
     * 즐겨찾기 토글
     */
    @Query("UPDATE business_cards SET isFavorite = :isFavorite WHERE id = :cardId")
    suspend fun toggleFavorite(cardId: String, isFavorite: Boolean)
}
