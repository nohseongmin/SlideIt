package com.example.slideit.util

import android.content.Context
import android.net.Uri
import com.example.slideit.data.model.BusinessCard
import com.google.gson.Gson
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * CSV 파일 입출력 유틸리티
 */
object CsvUtil {

    /**
     * 명함 데이터를 CSV로 내보내기
     */
    fun exportToCSV(context: Context, cards: List<BusinessCard>, uri: Uri): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = CSVWriter(OutputStreamWriter(outputStream))

                // 헤더 작성
                val header = arrayOf(
                    "ID", "이름", "직책", "부서", "회사", "이메일", "전화번호", "주소",
                    "배경색", "텍스트색", "강조색", "내명함여부", "즐겨찾기", "카테고리",
                    "태그", "메모", "웹사이트", "템플릿ID", "편집기타입", "캔버스데이터", "썸네일경로",
                    "생성일시", "수정일시"
                )
                writer.writeNext(header)

                // 데이터 작성
                cards.forEach { card ->
                    val data = arrayOf(
                        card.id,
                        card.name,
                        card.position,
                        card.department,
                        card.company,
                        card.email,
                        card.phone,
                        card.address,
                        card.backgroundColor.toString(),
                        card.textColor.toString(),
                        card.accentColor.toString(),
                        card.isMyCard.toString(),
                        card.isFavorite.toString(),
                        card.category,
                        Gson().toJson(card.tags),
                        card.memo,
                        card.websiteUrl,
                        card.templateId,
                        card.editorType,
                        card.canvasData ?: "",
                        card.thumbnailPath ?: "",
                        card.createdAt.toString(),
                        card.lastModifiedAt.toString()
                    )
                    writer.writeNext(data)
                }

                writer.close()
                Result.success(Unit)
            } ?: Result.failure(Exception("파일을 열 수 없습니다"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * CSV 파일에서 명함 데이터 가져오기
     */
    fun importFromCSV(context: Context, uri: Uri): Result<List<BusinessCard>> {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = CSVReader(InputStreamReader(inputStream, Charsets.UTF_8))
                val lines = reader.readAll()

                if (lines.isEmpty()) {
                    return Result.failure(Exception("CSV 파일이 비어있습니다"))
                }

                if (lines.size < 2) {
                    return Result.failure(Exception("CSV 파일에 데이터가 없습니다"))
                }

                // 헤더 스킵하고 데이터 파싱
                val cards = mutableListOf<BusinessCard>()
                var errorCount = 0

                lines.drop(1).forEachIndexed { index, line ->
                    try {
                        if (line.size < 23) {
                            errorCount++
                            return@forEachIndexed
                        }

                        val tags = try {
                            if (line[14].isNotEmpty() && line[14] != "[]") {
                                Gson().fromJson(line[14], Array<String>::class.java).toList()
                            } else {
                                emptyList()
                            }
                        } catch (e: Exception) {
                            emptyList()
                        }

                        val card = BusinessCard(
                            id = line[0].ifEmpty { java.util.UUID.randomUUID().toString() },
                            name = line[1],
                            position = line[2],
                            department = line[3],
                            company = line[4],
                            email = line[5],
                            phone = line[6],
                            address = line[7],
                            backgroundColor = line[8].toLongOrNull() ?: 0xFFFFFFFF,
                            textColor = line[9].toLongOrNull() ?: 0xFF1F1F1F,
                            accentColor = line[10].toLongOrNull() ?: 0xFF90CBFB,
                            isMyCard = line[11].toBoolean(),
                            isFavorite = line[12].toBoolean(),
                            category = line[13],
                            tags = tags,
                            memo = line[15],
                            websiteUrl = line[16],
                            templateId = line[17],
                            editorType = line.getOrNull(18)?.ifEmpty { "SIMPLE" } ?: "SIMPLE",
                            canvasData = line.getOrNull(19)?.ifEmpty { null },
                            thumbnailPath = line.getOrNull(20)?.ifEmpty { null },
                            createdAt = line.getOrNull(21)?.toLongOrNull() ?: System.currentTimeMillis(),
                            lastModifiedAt = line.getOrNull(22)?.toLongOrNull() ?: System.currentTimeMillis()
                        )
                        cards.add(card)
                    } catch (e: Exception) {
                        errorCount++
                        e.printStackTrace()
                    }
                }

                reader.close()

                if (cards.isEmpty() && errorCount > 0) {
                    Result.failure(Exception("CSV 파일 파싱 중 오류가 발생했습니다 (${errorCount}개 행 실패)"))
                } else {
                    Result.success(cards)
                }
            } ?: Result.failure(Exception("파일을 열 수 없습니다"))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("CSV 가져오기 실패: ${e.message}"))
        }
    }
}
