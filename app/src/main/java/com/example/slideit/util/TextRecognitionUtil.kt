package com.example.slideit.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.tasks.await

/**
 * ML Kit을 사용한 텍스트 인식 유틸리티
 */
object TextRecognitionUtil {

    /**
     * 이미지에서 텍스트 추출
     */
    suspend fun extractTextFromImage(context: Context, imageUri: Uri): String {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

            val result = recognizer.process(image).await()
            result.text
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 추출된 텍스트를 명함 정보로 파싱
     */
    fun parseBusinessCardText(text: String): ParsedCardInfo {
        val lines = text.lines().filter { it.isNotBlank() }

        var name = ""
        var position = ""
        var department = ""
        var company = ""
        var email = ""
        var phone = ""
        var address = ""

        lines.forEach { line ->
            val trimmedLine = line.trim()

            when {
                // 이메일 패턴
                trimmedLine.contains("@") && email.isEmpty() -> {
                    email = extractEmail(trimmedLine)
                }
                // 전화번호 패턴
                (trimmedLine.contains("-") || trimmedLine.startsWith("010") ||
                 trimmedLine.startsWith("02") || trimmedLine.startsWith("031")) && phone.isEmpty() -> {
                    phone = extractPhone(trimmedLine)
                }
                // 직책 키워드
                (trimmedLine.contains("대표") || trimmedLine.contains("이사") ||
                 trimmedLine.contains("부장") || trimmedLine.contains("과장") ||
                 trimmedLine.contains("대리") || trimmedLine.contains("사원") ||
                 trimmedLine.contains("팀장") || trimmedLine.contains("실장")) && position.isEmpty() -> {
                    position = trimmedLine
                }
                // 부서 키워드
                (trimmedLine.contains("팀") || trimmedLine.contains("부") ||
                 trimmedLine.contains("실")) && department.isEmpty() && position.isNotEmpty() -> {
                    department = trimmedLine
                }
                // 주소 키워드
                (trimmedLine.contains("시") || trimmedLine.contains("구") ||
                 trimmedLine.contains("동") || trimmedLine.contains("로")) && address.isEmpty() -> {
                    address = trimmedLine
                }
                // 회사명 (보통 긴 텍스트이거나 첫 줄)
                company.isEmpty() && trimmedLine.length > 2 -> {
                    company = trimmedLine
                }
            }
        }

        // 이름 추출 (보통 짧고, 2-4글자)
        if (name.isEmpty()) {
            lines.find { line ->
                val trimmed = line.trim()
                trimmed.length in 2..4 &&
                !trimmed.contains("@") &&
                !trimmed.contains("-") &&
                !trimmed.any { it.isDigit() }
            }?.let {
                name = it.trim()
            }
        }

        return ParsedCardInfo(
            name = name,
            position = position,
            department = department,
            company = company,
            email = email,
            phone = phone,
            address = address
        )
    }

    private fun extractEmail(text: String): String {
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailRegex.find(text)?.value ?: text
    }

    private fun extractPhone(text: String): String {
        // 숫자와 하이픈만 추출
        val phoneRegex = "[0-9-]+".toRegex()
        val matches = phoneRegex.findAll(text).map { it.value }.joinToString("")

        // 전화번호 포맷 정리
        return when {
            matches.length == 11 && matches.startsWith("010") -> {
                "${matches.substring(0, 3)}-${matches.substring(3, 7)}-${matches.substring(7)}"
            }
            matches.length == 10 && (matches.startsWith("02") || matches.startsWith("031")) -> {
                if (matches.startsWith("02")) {
                    "02-${matches.substring(2, 6)}-${matches.substring(6)}"
                } else {
                    "${matches.substring(0, 3)}-${matches.substring(3, 7)}-${matches.substring(7)}"
                }
            }
            else -> matches
        }
    }
}

/**
 * 파싱된 명함 정보
 */
data class ParsedCardInfo(
    val name: String,
    val position: String,
    val department: String,
    val company: String,
    val email: String,
    val phone: String,
    val address: String
)
