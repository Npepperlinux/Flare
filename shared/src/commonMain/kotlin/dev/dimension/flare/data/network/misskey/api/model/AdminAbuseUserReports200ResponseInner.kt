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
 * @param id * @param createdAt * @param comment * @param resolved * @param reporterId * @param targetUserId * @param assigneeId * @param reporter * @param targetUser * @param assignee */
@Serializable
data class AdminAbuseUserReports200ResponseInner(

    @SerialName(value = "id") val id: kotlin.String,

    @SerialName(value = "createdAt") val createdAt: kotlin.String,

    @SerialName(value = "comment") val comment: kotlin.String,

    @SerialName(value = "resolved") val resolved: kotlin.Boolean,

    @SerialName(value = "reporterId") val reporterId: kotlin.String,

    @SerialName(value = "targetUserId") val targetUserId: kotlin.String,

    @SerialName(value = "assigneeId") val assigneeId: kotlin.String? = null,

    @SerialName(value = "reporter") val reporter: User,

    @SerialName(value = "targetUser") val targetUser: User,

    @SerialName(value = "assignee") val assignee: User? = null,

)