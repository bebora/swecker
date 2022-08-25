package dev.bebora.swecker.data.alarm_browser

import androidx.room.*
import dev.bebora.swecker.data.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm")
    fun getAll(): Flow<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: Alarm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(alarm: List<Alarm>)

    @Delete
    suspend fun delete(alarm: Alarm)
}
