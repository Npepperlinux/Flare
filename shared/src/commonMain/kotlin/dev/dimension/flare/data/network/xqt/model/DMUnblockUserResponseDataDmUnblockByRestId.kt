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
 * @param typename
 * @param dmUnblockSuccessReason
 */
@Serializable
internal data class DMUnblockUserResponseDataDmUnblockByRestId(
    @SerialName(value = "__typename")
    val typename: kotlin.String? = null,
    @SerialName(value = "dm_unblock_success_reason")
    val dmUnblockSuccessReason: kotlin.String? = null,
)
