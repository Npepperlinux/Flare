package dev.dimension.flare.data.database.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbSearchHistory(
    @PrimaryKey
    val search: String,
    val created_at: Long,
)