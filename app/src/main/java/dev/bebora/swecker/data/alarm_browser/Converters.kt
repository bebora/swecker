package dev.bebora.swecker.data.alarm_browser

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class Converters {
    @TypeConverter
    fun fromOffsetDateTime(offsetDateTime: OffsetDateTime?): String? {
        return offsetDateTime?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @TypeConverter
    fun stringToOffsetDateTime(string: String?): OffsetDateTime? {
        return if (string == null) {
            null
        } else {
            OffsetDateTime.parse(string)
        }
    }

    @TypeConverter
    fun stringToLocalDate(string: String?): LocalDate? {
        return if (string == null) {
            null
        } else {
            LocalDate.parse(string)
        }
    }

    @TypeConverter
    fun fromLocalDate(offsetDateTime: LocalDate?): String? {
        return offsetDateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun stringToLocalTime(string: String?): LocalTime? {
        return if (string == null) {
            null
        } else {
            LocalTime.parse(string)
        }
    }

    @TypeConverter
    fun fromLocalTime(offsetDateTime: LocalTime?): String? {
        return offsetDateTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }


    @TypeConverter
    fun fromBooleanListToString(list: List<Boolean>) =
        list.joinToString(separator = ",") { b -> b.toString() }

    @TypeConverter
    fun fromStringToBooleanList(value: String) = value.split(",").map { a -> a.toBooleanStrict() }
}
