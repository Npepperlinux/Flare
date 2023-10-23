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
 * @param postId * @param title * @param fileIds * @param description * @param isSensitive */
@Serializable
data class GalleryPostsUpdateRequest(

    @SerialName(value = "postId") val postId: kotlin.String,

    @SerialName(value = "title") val title: kotlin.String,

    @SerialName(value = "fileIds") val fileIds: kotlin.collections.Set<kotlin.String>,

    @SerialName(value = "description") val description: kotlin.String? = null,

    @SerialName(value = "isSensitive") val isSensitive: kotlin.Boolean? = false,

)