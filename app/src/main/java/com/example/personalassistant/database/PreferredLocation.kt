package com.example.personalassistant.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "preferred_locations")
data class PreferredLocation(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "lat")
    val lat: Double,

    @ColumnInfo(name = "lng")
    val lng: Double,
)