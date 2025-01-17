package dev.dimension.flare.data.database.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.dimension.flare.model.PlatformType

@Entity
internal data class DbApplication(
    @PrimaryKey val host: String,
    val credential_json: String,
    val platform_type: PlatformType,
    val has_pending_oauth_request: Int = 0,
)
