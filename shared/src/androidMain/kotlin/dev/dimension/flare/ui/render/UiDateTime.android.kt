package dev.dimension.flare.ui.render

import android.text.format.DateUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime

public actual data class UiDateTime(
    val value: Instant,
//    val value: LocalizedShortTime,
) {
    public val shortTime: LocalizedShortTime by lazy {
        val compareTo = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val time = value.toLocalDateTime(timeZone)
        val diff = compareTo - value
        when {
            // dd MMM yy
            compareTo.toLocalDateTime(timeZone).year != time.year -> {
                LocalizedShortTime.YearMonthDay(time.toJavaLocalDateTime())
            }
            // dd MMM
            diff.inWholeDays >= 7 -> {
                LocalizedShortTime.MonthDay(time.toJavaLocalDateTime())
            }
            // xx day(s)
            diff.inWholeDays >= 1 -> {
                DateUtils
                    .getRelativeTimeSpanString(
                        value.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.DAY_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE,
                    ).toString()
                    .let(LocalizedShortTime::String)
            }
            // xx hr(s)
            diff.inWholeHours >= 1 -> {
                DateUtils
                    .getRelativeTimeSpanString(
                        value.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE,
                    ).toString()
                    .let(LocalizedShortTime::String)
            }
            // xx sec(s)
            diff.inWholeMinutes < 1 -> {
                DateUtils
                    .getRelativeTimeSpanString(
                        value.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE,
                    ).toString()
                    .let(LocalizedShortTime::String)
            }
            // xx min(s)
            else -> {
                DateUtils
                    .getRelativeTimeSpanString(
                        value.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE,
                    ).toString()
                    .let(LocalizedShortTime::String)
            }
        }
    }
}

internal actual fun Instant.toUi(): UiDateTime = UiDateTime(this)

public sealed interface LocalizedShortTime {
    public data class String(
        val value: kotlin.String,
    ) : LocalizedShortTime

    public data class YearMonthDay(
        val localDateTime: LocalDateTime,
    ) : LocalizedShortTime

    public data class MonthDay(
        val localDateTime: LocalDateTime,
    ) : LocalizedShortTime
}
