package com.example.foragingapplicationv2.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="forageSpot-table")
data class ForageSpotEntity(

    @PrimaryKey(autoGenerate = true)
    val spotID: Int = 0,

    val submittedUserID: String = "",
    val forageType: String = "",
    val notes: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val shared: Boolean = false,
    val addressDescription: String = ""


)
