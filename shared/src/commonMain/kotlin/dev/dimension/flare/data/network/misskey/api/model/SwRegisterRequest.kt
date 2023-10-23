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
 * @param endpoint * @param auth * @param publickey * @param sendReadMessage */
@Serializable
data class SwRegisterRequest(

    @SerialName(value = "endpoint") val endpoint: kotlin.String,

    @SerialName(value = "auth") val auth: kotlin.String,

    @SerialName(value = "publickey") val publickey: kotlin.String,

    @SerialName(value = "sendReadMessage") val sendReadMessage: kotlin.Boolean? = false,

)