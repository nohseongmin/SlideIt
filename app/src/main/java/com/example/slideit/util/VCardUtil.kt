package com.example.slideit.util

import com.example.slideit.data.model.BusinessCard

object VCardUtil {
    fun createVCardString(card: BusinessCard): String {
        return buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            // N (Name) - Separate name into last and first if possible
            // For simplicity, we'll put the full name in both FN and the first part of N
            val nameParts = card.name.split(" ", limit = 2)
            val lastName = nameParts.getOrNull(0) ?: ""
            val firstName = nameParts.getOrNull(1) ?: ""
            appendLine("N:$lastName;$firstName;;;")
            appendLine("FN:${card.name}")
            appendLine("ORG:${card.company}")
            appendLine("TITLE:${card.position}")
            appendLine("TEL;TYPE=CELL:${card.phone}")
            appendLine("EMAIL:${card.email}")
            // ADR (Address) - Assuming the whole address is in one field
            appendLine("ADR;TYPE=WORK:;;${card.address};;;;")
            appendLine("END:VCARD")
        }
    }
}
