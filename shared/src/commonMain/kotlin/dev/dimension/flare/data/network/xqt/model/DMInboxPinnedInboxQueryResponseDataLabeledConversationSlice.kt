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

package dev.dimension.flare.data.network.xqt.model

import dev.dimension.flare.data.network.xqt.model.DMInboxPinnedConversationItem
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 *
 *
 * @param items
 * @param sliceInfo
 */
@Serializable
internal data class DMInboxPinnedInboxQueryResponseDataLabeledConversationSlice(
    @SerialName(value = "items")
    val items: kotlin.collections.List<DMInboxPinnedConversationItem>? = null,
    @Contextual @SerialName(value = "slice_info")
    val sliceInfo: JsonObject? = null,
)
