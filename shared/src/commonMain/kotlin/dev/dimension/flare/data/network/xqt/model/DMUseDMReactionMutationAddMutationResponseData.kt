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

import dev.dimension.flare.data.network.xqt.model.DMUseDMReactionMutationAddMutationResponseDataCreateDmReaction
import dev.dimension.flare.data.network.xqt.model.DMUseDMReactionMutationAddMutationResponseDataDeleteDmReaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param createDmReaction
 * @param deleteDmReaction
 */
@Serializable
internal data class DMUseDMReactionMutationAddMutationResponseData(
    @SerialName(value = "create_dm_reaction")
    val createDmReaction: DMUseDMReactionMutationAddMutationResponseDataCreateDmReaction? = null,
    @SerialName(value = "delete_dm_reaction")
    val deleteDmReaction: DMUseDMReactionMutationAddMutationResponseDataDeleteDmReaction? = null,
)
