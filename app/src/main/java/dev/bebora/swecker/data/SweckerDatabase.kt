package dev.bebora.swecker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.bebora.swecker.data.alarm_browser.AlarmDao
import dev.bebora.swecker.data.alarm_browser.Converters

@Database(entities = [Alarm::class], version = 2)
@TypeConverters(Converters::class)
abstract class SweckerDatabase : RoomDatabase() {
    abstract val alarmDao: AlarmDao
}
