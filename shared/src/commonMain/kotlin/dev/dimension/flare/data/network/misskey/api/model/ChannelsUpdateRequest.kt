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
 * @param channelId * @param name * @param description * @param bannerId * @param isArchived * @param pinnedNoteIds * @param color */
@Serializable
data class ChannelsUpdateRequest(

    @SerialName(value = "channelId") val channelId: kotlin.String,

    @SerialName(value = "name") val name: kotlin.String? = null,

    @SerialName(value = "description") val description: kotlin.String? = null,

    @SerialName(value = "bannerId") val bannerId: kotlin.String? = null,

    @SerialName(value = "isArchived") val isArchived: kotlin.Boolean? = null,

    @SerialName(value = "pinnedNoteIds") val pinnedNoteIds: kotlin.collections.List<kotlin.String>? = null,

    @SerialName(value = "color") val color: kotlin.String? = null,

)