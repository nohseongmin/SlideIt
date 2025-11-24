package com.example.slideit.data.model

/**
 * 명함 템플릿 데이터 클래스
 */
data class CardTemplate(
    val id: String,
    val name: String,
    val backgroundColor: Long,
    val textColor: Long,
    val accentColor: Long,
    val description: String
)

/**
 * 기본 템플릿 목록
 */
object CardTemplates {
    val templates = listOf(
        CardTemplate(
            id = "default",
            name = "클래식 화이트",
            backgroundColor = 0xFFFFFFFF,
            textColor = 0xFF1F1F1F,
            accentColor = 0xFF90CBFB,
            description = "깔끔한 화이트 배경"
        ),
        CardTemplate(
            id = "business_blue",
            name = "비즈니스 블루",
            backgroundColor = 0xFF1E3A8A,
            textColor = 0xFFFFFFFF,
            accentColor = 0xFF60A5FA,
            description = "전문적인 파란색"
        ),
        CardTemplate(
            id = "modern_black",
            name = "모던 블랙",
            backgroundColor = 0xFF0F172A,
            textColor = 0xFFFFFFFF,
            accentColor = 0xFF38BDF8,
            description = "세련된 검은색"
        ),
        CardTemplate(
            id = "elegant_purple",
            name = "우아한 퍼플",
            backgroundColor = 0xFF6B21A8,
            textColor = 0xFFFFFFFF,
            accentColor = 0xFFC084FC,
            description = "고급스러운 보라색"
        ),
        CardTemplate(
            id = "creative_gradient",
            name = "크리에이티브 그라디언트",
            backgroundColor = 0xFFEC4899,
            textColor = 0xFFFFFFFF,
            accentColor = 0xFFFCE7F3,
            description = "활기찬 핑크"
        ),
        CardTemplate(
            id = "professional_gray",
            name = "프로페셔널 그레이",
            backgroundColor = 0xFF374151,
            textColor = 0xFFFFFFFF,
            accentColor = 0xFF9CA3AF,
            description = "차분한 회색"
        ),
        CardTemplate(
            id = "nature_green",
            name = "네이처 그린",
            backgroundColor = 0xFF065F46,
            textColor = 0xFFFFFFFF,
            accentColor = 0xFF34D399,
            description = "자연스러운 초록색"
        ),
        CardTemplate(
            id = "warm_orange",
            name = "웜 오렌지",
            backgroundColor = 0xFFC2410C,
            textColor = 0xFFFFFFFF,
            accentColor = 0xFFFED7AA,
            description = "따뜻한 주황색"
        )
    )

    fun getTemplateById(id: String): CardTemplate? {
        return templates.find { it.id == id }
    }
}
