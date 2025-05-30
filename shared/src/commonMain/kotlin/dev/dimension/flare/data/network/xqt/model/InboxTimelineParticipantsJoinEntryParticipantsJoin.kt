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

import dev.dimension.flare.data.network.xqt.model.InboxTimelineParticipantsJoinEntryParticipantsJoinParticipantsInner
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param id
 * @param time
 * @param affectsSort
 * @param conversationId
 * @param senderId
 * @param participants
 */
@Serializable
internal data class InboxTimelineParticipantsJoinEntryParticipantsJoin(
    @SerialName(value = "id")
    val id: kotlin.String? = null,
    @SerialName(value = "time")
    val time: kotlin.String? = null,
    @SerialName(value = "affects_sort")
    val affectsSort: kotlin.Boolean? = null,
    @SerialName(value = "conversation_id")
    val conversationId: kotlin.String? = null,
    @SerialName(value = "sender_id")
    val senderId: kotlin.String? = null,
    @SerialName(value = "participants")
    val participants: kotlin.collections.List<InboxTimelineParticipantsJoinEntryParticipantsJoinParticipantsInner>? = null,
)
