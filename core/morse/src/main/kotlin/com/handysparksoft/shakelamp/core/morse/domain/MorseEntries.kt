package com.handysparksoft.shakelamp.core.morse.domain

enum class MorseEntryGroup {
    Letters,
    Numbers,
    Punctuation,
    All,
}

object MorseEntries {
    fun forGroup(group: MorseEntryGroup): List<MorseEntry> =
        when (group) {
            MorseEntryGroup.Letters -> letters()
            MorseEntryGroup.Numbers -> numbers()
            MorseEntryGroup.Punctuation -> punctuation()
            MorseEntryGroup.All -> letters() + numbers() + punctuation()
        }

    private fun letters() =
        listOf(
            MorseEntry("A", ".-"),
            MorseEntry("B", "-..."),
            MorseEntry("C", "-.-."),
            MorseEntry("D", "-.."),
            MorseEntry("E", "."),
            MorseEntry("F", "..-."),
            MorseEntry("G", "--."),
            MorseEntry("H", "...."),
            MorseEntry("I", ".."),
            MorseEntry("J", ".---"),
            MorseEntry("K", "-.-"),
            MorseEntry("L", ".-.."),
            MorseEntry("M", "--"),
            MorseEntry("N", "-."),
            MorseEntry("O", "---"),
            MorseEntry("P", ".--."),
            MorseEntry("Q", "--.-"),
            MorseEntry("R", ".-."),
            MorseEntry("S", "..."),
            MorseEntry("T", "-"),
            MorseEntry("U", "..-"),
            MorseEntry("V", "...-"),
            MorseEntry("W", ".--"),
            MorseEntry("X", "-..-"),
            MorseEntry("Y", "-.--"),
            MorseEntry("Z", "--.."),
        )

    private fun numbers() =
        listOf(
            MorseEntry("0", "-----"),
            MorseEntry("1", ".----"),
            MorseEntry("2", "..---"),
            MorseEntry("3", "...--"),
            MorseEntry("4", "....-"),
            MorseEntry("5", "....."),
            MorseEntry("6", "-...."),
            MorseEntry("7", "--..."),
            MorseEntry("8", "---.."),
            MorseEntry("9", "----."),
        )

    private fun punctuation() =
        listOf(
            MorseEntry(".", ".-.-.-"),
            MorseEntry(",", "--..--"),
            MorseEntry("?", "..--.."),
            MorseEntry("'", ".----."),
            MorseEntry("!", "-.-.--"),
            MorseEntry("/", "-..-."),
            MorseEntry("(", "-.--."),
            MorseEntry(")", "-.--.-"),
            MorseEntry("&", ".-..."),
            MorseEntry(":", "---..."),
            MorseEntry(";", "-.-.-."),
            MorseEntry("=", "-...-"),
            MorseEntry("+", ".-.-."),
            MorseEntry("-", "-....-"),
            MorseEntry("\"", ".-..-."),
            MorseEntry("@", ".--.-."),
            MorseEntry("$", "...-..-"),
            MorseEntry("_", "..--.-"),
        )
}
