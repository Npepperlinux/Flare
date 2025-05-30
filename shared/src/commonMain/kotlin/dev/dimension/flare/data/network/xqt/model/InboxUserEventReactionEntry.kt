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
 * @param id
 * @param time
 * @param affectsSort
 * @param conversationId
 * @param senderId
 * @param messageId
 * @param reaction
 * @param reactionKey
 * @param emojiReaction
 */
@Serializable
internal data class InboxUserEventReactionEntry(
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
    @SerialName(value = "message_id")
    val messageId: kotlin.String? = null,
    @SerialName(value = "reaction")
    val reaction: kotlin.String? = null,
    @SerialName(value = "reaction_key")
    val reactionKey: kotlin.String? = null,
    @SerialName(value = "emoji_reaction")
    val emojiReaction: kotlin.String? = null,
)
