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
 * @param messageId
 * @param messageCreateEventId
 */
@Serializable
internal data class InboxTimelineMessageDeleteORMarkAsSpamEntryMessagesInner(
    @SerialName(value = "message_id")
    val messageId: kotlin.String? = null,
    @SerialName(value = "message_create_event_id")
    val messageCreateEventId: kotlin.String? = null,
)
