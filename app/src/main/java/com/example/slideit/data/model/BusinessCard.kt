package com.example.slideit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

/**
 * 명함 데이터 모델
 */
@Entity(tableName = "business_cards")
@TypeConverters(Converters::class)
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
    val isFavorite: Boolean = false, // 즐겨찾기
    val category: String = "", // 카테고리
    val tags: List<String> = emptyList(), // 태그
    val memo: String = "", // 메모
    val websiteUrl: String = "", // 웹사이트
    val templateId: String = "default", // 템플릿 ID

    // 새로 추가된 필드들
    val editorType: String = "SIMPLE", // "SIMPLE" or "CANVAS"
    val canvasData: String? = null, // JSON 형태의 CanvasCardData
    val thumbnailPath: String? = null, // 렌더링된 이미지 경로

    val createdAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis()
)

/**
 * Room TypeConverter for List<String>
 */
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
