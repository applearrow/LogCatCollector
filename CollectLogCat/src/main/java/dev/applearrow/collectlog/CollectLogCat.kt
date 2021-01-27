package dev.applearrow.collectlog

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.ZoneId

const val SYNC_TAG = "MainActivity"

fun collectLatestLogs(
    resetString: String,
    trimLine: Boolean = true
): SpannableStringBuilder {
    // NOTE: Remember that either -d or -t has to be present in the arguments!
    val command = "logcat ${SYNC_TAG} *:S -d "
    val process = Runtime.getRuntime().exec(command)
    val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
    return collectLogs(bufferedReader, trimLine, resetString)
}


fun collectLogsSince(
    ldt: LocalDateTime,
    trimLine: Boolean = true
): SpannableStringBuilder {
    // NOTE: Remember that either -d or -t has to be present in the arguments!
    val epoch = ldt.atZone(ZoneId.systemDefault()).toEpochSecond()
    val command = "logcat ${SYNC_TAG}:I *:S -d -t $epoch.000"
    val process = Runtime.getRuntime().exec(command)
    val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
    return collectLogs(bufferedReader, trimLine)
}

fun collectLogs(
    bufferedReader: BufferedReader,
    trimLine: Boolean = true,
    resetString: String? = null
): SpannableStringBuilder {
    val log = StringBuilder()
    try {
        var line = bufferedReader.readLine()
        while (line != null) {
            if (line.contains(SYNC_TAG)) {
                val info =
                    if (trimLine) line.substringAfter("${SYNC_TAG} :") else line
                log.append(info).appendLine()
                if (resetString != null && line.contains(resetString)) log.setLength(0)
            }
            line = bufferedReader.readLine()
        }
    } catch (e: IOException) {
        e.message?.let { Log.e(SYNC_TAG, it) }
    }

    return log.toString()
        .boldLinesThatStartWith(
            " save",
            " delete"
        )
}

/** Bold each line that starts with any of the provided prefixes **/
fun String.boldLinesThatStartWith(
    prefix1: String,
    prefix2: String
): SpannableStringBuilder {
    val stylesLines = this.lines().map {
        if (it.startsWith(prefix1) || it.startsWith(prefix2)) SimpleSpanBuilder.Span(
            "$it\n",
            ForegroundColorSpan(Color.BLACK)
        ) else SimpleSpanBuilder.Span("$it\n")
    }
    return stylesLines.fold(SimpleSpanBuilder()) { total, line -> total.plus(line) }.build()
}
