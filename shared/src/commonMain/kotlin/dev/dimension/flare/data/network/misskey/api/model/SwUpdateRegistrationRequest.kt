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
 * @param endpoint * @param sendReadMessage */
@Serializable
data class SwUpdateRegistrationRequest(

    @SerialName(value = "endpoint") val endpoint: kotlin.String,

    @SerialName(value = "sendReadMessage") val sendReadMessage: kotlin.Boolean? = null,

)