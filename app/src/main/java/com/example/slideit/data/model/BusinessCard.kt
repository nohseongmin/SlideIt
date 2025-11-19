package com.example.slideit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * 명함 데이터 모델
 */
@Entity(tableName = "business_cards")
data class BusinessCard(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val position: String,
    val department: String,
    val company: String,
    val email: String,
    val phone: String,
    val address: String,
    val imageUrl: String? = null,
    val backgroundColor: Long = 0xFFFFFFFF, // 배경색 (ARGB)
    val textColor: Long = 0xFF1F1F1F, // 텍스트 색상
    val accentColor: Long = 0xFF90CBFB, // 강조 색상
    val isMyCard: Boolean = false, // 내 명함 여부
    val createdAt: Long = System.currentTimeMillis()
)
