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
 * @param limit * @param sinceId * @param untilId * @param state * @param reporterOrigin * @param targetUserOrigin * @param forwarded */
@Serializable

data class AdminAbuseUserReportsRequest(

    @SerialName(value = "limit") val limit: kotlin.Int? = 10,

    @SerialName(value = "sinceId") val sinceId: kotlin.String? = null,

    @SerialName(value = "untilId") val untilId: kotlin.String? = null,

    @SerialName(value = "state") val state: kotlin.String? = null,

    @SerialName(value = "reporterOrigin") val reporterOrigin: AdminAbuseUserReportsRequest.ReporterOrigin? = ReporterOrigin.Combined,

    @SerialName(value = "targetUserOrigin") val targetUserOrigin: AdminAbuseUserReportsRequest.TargetUserOrigin? = TargetUserOrigin.Combined,

    @SerialName(value = "forwarded") val forwarded: kotlin.Boolean? = false

) {

    /**
     * *
     * Values: Combined,Local,Remote
     */
    @Serializable
    enum class ReporterOrigin(val value: kotlin.String) {
        @SerialName(value = "combined")
        Combined("combined"),

        @SerialName(value = "local")
        Local("local"),

        @SerialName(value = "remote")
        Remote("remote");
    }

    /**
     * *
     * Values: Combined,Local,Remote
     */
    @Serializable
    enum class TargetUserOrigin(val value: kotlin.String) {
        @SerialName(value = "combined")
        Combined("combined"),

        @SerialName(value = "local")
        Local("local"),

        @SerialName(value = "remote")
        Remote("remote");
    }
}