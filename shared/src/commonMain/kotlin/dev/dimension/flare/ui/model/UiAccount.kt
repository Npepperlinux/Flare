package dev.dimension.flare.ui.model

import androidx.compose.runtime.Immutable
import dev.dimension.flare.common.decodeJson
import dev.dimension.flare.data.database.app.DbAccount
import dev.dimension.flare.data.datasource.bluesky.BlueskyDataSource
import dev.dimension.flare.data.datasource.mastodon.MastodonDataSource
import dev.dimension.flare.data.datasource.misskey.MisskeyDataSource
import dev.dimension.flare.data.datasource.xqt.XQTDataSource
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.model.PlatformType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
sealed interface UiAccount {
    val accountKey: MicroBlogKey
    val platformType: PlatformType
    val credential: Credential

    @Immutable
    @Serializable
    sealed interface Credential

    @Immutable
    data class Mastodon(
        override val credential: Credential,
        override val accountKey: MicroBlogKey,
    ) : UiAccount {
        override val platformType: PlatformType
            get() = PlatformType.Mastodon

        @Immutable
        @Serializable
        @SerialName("MastodonCredential")
        data class Credential(
            val instance: String,
            val accessToken: String,
        ) : UiAccount.Credential

        val dataSource by lazy {
            MastodonDataSource(this)
        }
    }

    @Immutable
    data class Misskey(
        override val credential: Credential,
        override val accountKey: MicroBlogKey,
    ) : UiAccount {
        override val platformType: PlatformType
            get() = PlatformType.Misskey

        @Immutable
        @Serializable
        @SerialName("MisskeyCredential")
        data class Credential(
            val host: String,
            val accessToken: String,
        ) : UiAccount.Credential

        val dataSource by lazy {
            MisskeyDataSource(this)
        }
    }

    @Immutable
    data class Bluesky(
        override val credential: Credential,
        override val accountKey: MicroBlogKey,
    ) : UiAccount {
        override val platformType: PlatformType
            get() = PlatformType.Bluesky

        @Immutable
        @Serializable
        @SerialName("BlueskyCredential")
        data class Credential(
            val baseUrl: String,
            val accessToken: String,
            val refreshToken: String,
        ) : UiAccount.Credential

        val dataSource by lazy {
            BlueskyDataSource(this)
        }
    }

    @Immutable
    data class XQT(
        override val accountKey: MicroBlogKey,
        override val credential: Credential,
    ) : UiAccount {
        override val platformType: PlatformType
            get() = PlatformType.xQt

        @Immutable
        @Serializable
        @SerialName("XQTCredential")
        data class Credential(
            val chocolate: String,
        ) : UiAccount.Credential

        val dataSource by lazy {
            XQTDataSource(this)
        }
    }

    companion object {
        fun DbAccount.toUi(): UiAccount =
            when (platform_type) {
                PlatformType.Mastodon -> {
                    val credential = credential_json.decodeJson<Mastodon.Credential>()
                    Mastodon(
                        credential = credential,
                        accountKey = account_key,
                    )
                }

                PlatformType.Misskey -> {
                    val credential = credential_json.decodeJson<Misskey.Credential>()
                    Misskey(
                        credential = credential,
                        accountKey = account_key,
                    )
                }

                PlatformType.Bluesky -> {
                    val credential = credential_json.decodeJson<Bluesky.Credential>()
                    Bluesky(
                        credential = credential,
                        accountKey = account_key,
                    )
                }

                PlatformType.xQt -> {
                    val credential = credential_json.decodeJson<XQT.Credential>()
                    XQT(
                        credential = credential,
                        accountKey = account_key,
                    )
                }
            }
    }
}
