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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param userId
 * @param lastReadEventId
 */
@Serializable
internal data class InboxConversationParticipantsInner(
    @SerialName(value = "user_id")
    val userId: kotlin.String? = null,
    @SerialName(value = "last_read_event_id")
    val lastReadEventId: kotlin.String? = null,
)
