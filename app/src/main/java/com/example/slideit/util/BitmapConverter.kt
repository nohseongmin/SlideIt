package com.example.slideit.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.ui.components.CardRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Bitmap 변환 유틸리티
 *
 * Composable을 Bitmap으로 변환하고 저장/공유하는 기능 제공
 */
object BitmapConverter {

    /**
     * 명함을 Bitmap으로 변환
     *
     * @param context Context
     * @param card 변환할 명함
     * @param width 이미지 너비 (픽셀)
     * @param height 이미지 높이 (픽셀)
     * @return Bitmap
     */
    suspend fun cardToBitmap(
        context: Context,
        card: BusinessCard,
        width: Int = 1080,
        height: Int = 1700
    ): Bitmap = withContext(Dispatchers.Main) {
        // ComposeView 생성
        val composeView = ComposeView(context).apply {
            layoutParams = ViewGroup.LayoutParams(width, height)
            setContent {
                CardRenderer(
                    card = card,
                    showRotated = false
                )
            }
        }

        // View를 측정하고 배치
        composeView.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        composeView.layout(0, 0, width, height)

        // Bitmap 생성
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        composeView.draw(canvas)

        bitmap
    }

    /**
     * Bitmap을 내부 저장소에 저장
     *
     * @param context Context
     * @param bitmap 저장할 Bitmap
     * @param fileName 파일 이름 (확장자 제외)
     * @return 저장된 파일 경로
     */
    suspend fun saveBitmapToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): String = withContext(Dispatchers.IO) {
        // 내부 저장소 디렉토리 생성
        val cardsDir = File(context.filesDir, "cards")
        if (!cardsDir.exists()) {
            cardsDir.mkdirs()
        }

        // 파일 저장
        val file = File(cardsDir, "$fileName.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        file.absolutePath
    }

    /**
     * Bitmap을 갤러리에 저장
     *
     * @param context Context
     * @param bitmap 저장할 Bitmap
     * @param fileName 파일 이름 (확장자 제외)
     * @return 저장 성공 여부
     */
    suspend fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "SlideIT_${System.currentTimeMillis()}"
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SlideIT")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val imageUri = contentResolver.insert(imageCollection, contentValues)
            imageUri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Bitmap을 임시 파일로 저장하고 공유용 URI 생성
     *
     * @param context Context
     * @param bitmap 공유할 Bitmap
     * @param fileName 파일 이름 (확장자 제외)
     * @return 공유용 URI
     */
    suspend fun saveBitmapForSharing(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "business_card"
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            // 캐시 디렉토리에 저장
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()

            val file = File(cachePath, "$fileName.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // FileProvider를 통해 URI 생성
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 명함 공유 Intent 생성
     *
     * @param context Context
     * @param card 공유할 명함
     * @return 공유 Intent
     */
    suspend fun createShareIntent(
        context: Context,
        card: BusinessCard
    ): Intent? = withContext(Dispatchers.IO) {
        try {
            // 명함을 Bitmap으로 변환
            val bitmap = cardToBitmap(context, card)

            // 임시 파일로 저장
            val uri = saveBitmapForSharing(
                context,
                bitmap,
                "business_card_${card.name.replace(" ", "_")}"
            )

            uri?.let {
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, it)
                    putExtra(
                        Intent.EXTRA_TEXT,
                        """
                        ${card.name}
                        ${card.position} ${if (card.department.isNotEmpty()) "| ${card.department}" else ""}
                        ${card.company}
                        ${card.phone}
                        ${card.email}
                        """.trimIndent()
                    )
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 명함을 이미지로 저장 (내부 저장소 + 갤러리)
     *
     * @param context Context
     * @param card 저장할 명함
     * @return 저장 성공 여부
     */
    suspend fun saveCardAsImage(
        context: Context,
        card: BusinessCard
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // 명함을 Bitmap으로 변환
            val bitmap = cardToBitmap(context, card)

            // 내부 저장소에 저장
            val fileName = "card_${card.id}"
            saveBitmapToInternalStorage(context, bitmap, fileName)

            // 갤러리에도 저장
            saveBitmapToGallery(
                context,
                bitmap,
                "SlideIT_${card.name.replace(" ", "_")}_${System.currentTimeMillis()}"
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 내부 저장소에서 명함 이미지 로드
     *
     * @param context Context
     * @param cardId 명함 ID
     * @return Bitmap 또는 null
     */
    fun loadCardImage(
        context: Context,
        cardId: String
    ): Bitmap? {
        return try {
            val file = File(context.filesDir, "cards/card_$cardId.png")
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 명함 이미지 삭제
     *
     * @param context Context
     * @param cardId 명함 ID
     * @return 삭제 성공 여부
     */
    fun deleteCardImage(
        context: Context,
        cardId: String
    ): Boolean {
        return try {
            val file = File(context.filesDir, "cards/card_$cardId.png")
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
