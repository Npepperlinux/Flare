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
 * @param query * @param offset * @param limit * @param origin * @param detail */
@Serializable

data class UsersSearchRequest(

    @SerialName(value = "query") val query: kotlin.String,

    @SerialName(value = "offset") val offset: kotlin.Int? = 0,

    @SerialName(value = "limit") val limit: kotlin.Int? = 10,

    @SerialName(value = "origin") val origin: UsersSearchRequest.Origin? = Origin.Combined,

    @SerialName(value = "detail") val detail: kotlin.Boolean? = true

) {

    /**
     * *
     * Values: Local,Remote,Combined
     */
    @Serializable
    enum class Origin(val value: kotlin.String) {
        @SerialName(value = "local")
        Local("local"),

        @SerialName(value = "remote")
        Remote("remote"),

        @SerialName(value = "combined")
        Combined("combined");
    }
}