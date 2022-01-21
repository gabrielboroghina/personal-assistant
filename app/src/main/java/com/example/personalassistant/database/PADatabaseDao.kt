package com.example.personalassistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface PADatabaseDao {

    @Insert
    suspend fun insert(entry: PreferredLocation)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param entry new value to write
     */
    @Update
    suspend fun update(entry: PreferredLocation)

    /**
     * Gets the preferred location with a given name
     *
     * @param key
     */
    @Query("SELECT * from preferred_locations WHERE name = :name")
    suspend fun getByName(name: String): PreferredLocation?

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM preferred_locations")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM preferred_locations ORDER BY id DESC")
    suspend fun getAll(): List<PreferredLocation>
}
