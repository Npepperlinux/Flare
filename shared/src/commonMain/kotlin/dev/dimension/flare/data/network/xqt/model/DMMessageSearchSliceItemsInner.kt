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

import dev.dimension.flare.data.network.xqt.model.DMEvent
import dev.dimension.flare.data.network.xqt.model.DMGroupSearchSliceItemsInnerHighlighting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param dmEvent
 * @param highlighting
 */
@Serializable
internal data class DMMessageSearchSliceItemsInner(
    @SerialName(value = "dm_event")
    val dmEvent: DMEvent? = null,
    @SerialName(value = "highlighting")
    val highlighting: DMGroupSearchSliceItemsInnerHighlighting? = null,
)
