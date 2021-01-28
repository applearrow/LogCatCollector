package dev.applearrow.logcat

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.ZoneId


class LogCatCollector(private val tag: String) {

    fun collect(
        resetString: String,
        trimLine: Boolean = true,
        boldPrefixes: List<String> = listOf()
    ): SpannableStringBuilder {
        // NOTE: Remember that either -d or -t has to be present in the arguments!
        val command = "logcat $tag *:S -d "
        val process = Runtime.getRuntime().exec(command)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        return internalCollect(bufferedReader, trimLine, resetString, boldPrefixes)
    }


    fun collectSince(
        ldt: LocalDateTime,
        trimLine: Boolean = true,
        boldPrefixes: List<String> = listOf()
    ): SpannableStringBuilder {
        // NOTE: Remember that either -d or -t has to be present in the arguments!
        val epoch = ldt.atZone(ZoneId.systemDefault()).toEpochSecond()
        val command = "logcat $tag:I *:S -d -t $epoch.000"
        val process = Runtime.getRuntime().exec(command)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        return internalCollect(bufferedReader, trimLine, boldPrefixes = boldPrefixes)
    }

    private fun internalCollect(
        bufferedReader: BufferedReader,
        trimLine: Boolean = true,
        resetString: String? = null,
        boldPrefixes: List<String> = listOf()
    ): SpannableStringBuilder {
        val log = StringBuilder()
        try {
            var line = bufferedReader.readLine()
            while (line != null) {
                if (line.contains(tag)) {
                    val info =
                        if (trimLine) line.substringAfter("$tag: ") else line
                    log.append(info).appendLine()
                    if (resetString != null && line.contains(resetString)) log.setLength(0)
                }
                line = bufferedReader.readLine()
            }
        } catch (e: IOException) {
            e.message?.let { Log.e(TAG, it) }
        }

        return log.toString().boldLinesThatStartWith(boldPrefixes)
    }

    /**
     * Bold each line that starts with any of the provided prefixes
     **/
    private fun String.boldLinesThatStartWith(
        prefixes: List<String>
    ): SpannableStringBuilder {
        val stylesLines = this.lines().map { line ->
            if (line.startsWithAny(prefixes))
                SimpleSpanBuilder.Span(
                    "$line\n", ForegroundColorSpan(Color.BLACK)
                ) else
                SimpleSpanBuilder.Span("$line\n")
        }
        return stylesLines.fold(SimpleSpanBuilder()) { total, line -> total.plus(line) }.build()
    }

    private fun String.startsWithAny(prefixes: List<String>) = prefixes.any { startsWith(it) }

    companion object {
        const val TAG = "LogCatCollector"
    }
}