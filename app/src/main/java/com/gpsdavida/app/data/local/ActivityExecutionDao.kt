package com.gpsdavida.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ActivityExecutionDao {
    @Upsert
    suspend fun upsert(entity: ActivityExecutionEntity)

    @Query("SELECT * FROM activity_executions WHERE activityInstanceId = :id LIMIT 1")
    suspend fun getById(id: String): ActivityExecutionEntity?
}
