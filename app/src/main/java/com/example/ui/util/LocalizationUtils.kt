package com.example.ui.util

import com.example.data.model.Language
import java.text.NumberFormat
import java.util.Locale

object LocalizationUtils {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun formatPrice(amount: Long, lang: Language): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        val formatted = formatter.format(amount)
        return if (lang == Language.FA) {
            val persianFormatted = toPersianDigits(formatted)
            "$persianFormatted تومان"
        } else {
            "$formatted Toman"
        }
    }

    fun formatNumber(number: Any, lang: Language): String {
        val s = number.toString()
        return if (lang == Language.FA) {
            toPersianDigits(s)
        } else {
            s
        }
    }

    fun toPersianDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
