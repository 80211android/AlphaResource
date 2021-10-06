package com.movieplayer.android.utils

import java.util.regex.Pattern

class RegexUtils {
    companion object {
        val shared = RegexUtils()
    }

    private fun findFirstIndexOfEngCharacter(s: String): Int {
        val m = Pattern.compile("[A-Za-z]").matcher(s)
        if (m.find()) {
            return m.start()
        }
        return -1
    }

    private fun isContainEngCharacter(line: String): Boolean {
        return line.matches(".*[a-zA-Z]+.*".toRegex())
    }

    // Removing all alphabetic Character from String
    private fun keepAllNumericValues(s: String): String {
        return s.replace("[^\\d.]".toRegex(), "").trim()
    }

    // Removing all numeric Character from String
    private fun removeAllNumericValues(s: String): String {
        return s.replace("[\\d.]".toRegex(), "").trim()
    }

    private fun isContainDate(userInput: String): Boolean {
        val p = Pattern.compile("\\d{1,2} [A-Z]{3} \\d{4}") // 31 Dec 1994
        val m = p.matcher(userInput)
        return m.find()
    }
}