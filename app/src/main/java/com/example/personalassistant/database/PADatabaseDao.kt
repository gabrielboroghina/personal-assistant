package com.example.personalassistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface PADatabaseDao {

    @Insert
    suspend fun insert(entry: PrefferedLocation)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param entry new value to write
     */
    @Update
    suspend fun update(entry: PrefferedLocation)

    /**
     * Selects ...
     *
     * @param key
     */
    @Query("SELECT * from preffered_locations WHERE name = :name")
    suspend fun getByName(name: String): PrefferedLocation?

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM preffered_locations")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM preffered_locations ORDER BY id DESC")
    suspend fun getAll(): List<PrefferedLocation>
}
