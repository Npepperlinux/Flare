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
    "UnusedImport",
)

package dev.dimension.flare.data.network.misskey.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * *
 * @param id * @param name * @param aliases * @param fileId * @param category Use `null` to reset the category.
 * @param license * @param isSensitive * @param localOnly * @param roleIdsThatCanBeUsedThisEmojiAsReaction */
@Serializable
data class AdminEmojiUpdateRequest(

    @SerialName(value = "id") val id: kotlin.String,

    @SerialName(value = "name") val name: kotlin.String,

    @SerialName(value = "aliases") val aliases: kotlin.collections.List<kotlin.String>,

    @SerialName(value = "fileId") val fileId: kotlin.String? = null,

    /* Use `null` to reset the category. */
    @SerialName(value = "category") val category: kotlin.String? = null,

    @SerialName(value = "license") val license: kotlin.String? = null,

    @SerialName(value = "isSensitive") val isSensitive: kotlin.Boolean? = null,

    @SerialName(value = "localOnly") val localOnly: kotlin.Boolean? = null,

    @SerialName(value = "roleIdsThatCanBeUsedThisEmojiAsReaction") val roleIdsThatCanBeUsedThisEmojiAsReaction: kotlin.collections.List<kotlin.String>? = null,

)