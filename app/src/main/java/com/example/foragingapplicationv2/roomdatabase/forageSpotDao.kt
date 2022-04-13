package com.example.foragingapplicationv2.roomdatabase

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ForageSpotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForageSpot(forageSpot: ForageSpotEntity)

    @Delete
    suspend fun deleteForageSpot(forageSpotEntity: ForageSpotEntity)

    @Query("SELECT * FROM `forageSpot-table` WHERE submittedUserID=:id")
    fun fetchSpotsByUser(id: String): List<ForageSpotEntity>

    @Query("SELECT * FROM `forageSpot-table` WHERE submittedUserID=:id AND forageType=:forage")
    fun fetchSpotsByUserandForage(id: String, forage: String): List<ForageSpotEntity>


    @Query("SELECT * FROM `forageSpot-table` WHERE spotID=:id")
    fun fetchSpotBySpotID(id: Int): ForageSpotEntity


}