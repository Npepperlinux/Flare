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

import dev.dimension.flare.data.network.xqt.model.DMGroupSearchSliceSliceInfo
import dev.dimension.flare.data.network.xqt.model.DMMessageSearchSliceItemsInner
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param typename
 * @param sliceInfo
 * @param items
 */
@Serializable
internal data class DMMessageSearchSlice(
    @SerialName(value = "__typename")
    val typename: kotlin.String? = null,
    @SerialName(value = "sliceInfo")
    val sliceInfo: DMGroupSearchSliceSliceInfo? = null,
    @SerialName(value = "items")
    val items: kotlin.collections.List<DMMessageSearchSliceItemsInner>? = null,
)
