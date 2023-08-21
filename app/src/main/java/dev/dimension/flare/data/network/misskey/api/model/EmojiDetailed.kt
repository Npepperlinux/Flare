/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package dev.dimension.flare.data.network.misskey.api.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * *
 * @param id * @param aliases * @param name * @param category * @param host The local host is represented with `null`.
 * @param url * @param license * @param isSensitive * @param localOnly * @param roleIdsThatCanBeUsedThisEmojiAsReaction */
@Serializable

data class EmojiDetailed(

    @SerialName(value = "id") val id: kotlin.String,

    @SerialName(value = "aliases") val aliases: kotlin.collections.List<kotlin.String>,

    @SerialName(value = "name") val name: kotlin.String,

    @SerialName(value = "category") val category: kotlin.String? = null,

    /* The local host is represented with `null`. */
    @SerialName(value = "host") val host: kotlin.String? = null,

    @SerialName(value = "url") val url: kotlin.String,

    @SerialName(value = "license") val license: kotlin.String? = null,

    @SerialName(value = "isSensitive") val isSensitive: kotlin.Boolean,

    @SerialName(value = "localOnly") val localOnly: kotlin.Boolean,

    @SerialName(value = "roleIdsThatCanBeUsedThisEmojiAsReaction") val roleIdsThatCanBeUsedThisEmojiAsReaction: kotlin.collections.List<kotlin.String>

)