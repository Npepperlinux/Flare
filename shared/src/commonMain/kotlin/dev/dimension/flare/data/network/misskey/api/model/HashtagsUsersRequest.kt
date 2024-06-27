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
 * @param tag * @param sort * @param limit * @param state * @param origin */
@Serializable
internal data class HashtagsUsersRequest(
    @SerialName(value = "tag") val tag: kotlin.String,
    @SerialName(value = "sort") val sort: HashtagsUsersRequest.Sort,
    @SerialName(value = "limit") val limit: kotlin.Int? = 10,
    @SerialName(value = "state") val state: HashtagsUsersRequest.State? = State.All,
    @SerialName(value = "origin") val origin: HashtagsUsersRequest.Origin? = Origin.Local,
) {
    /**
     * *
     * Values: PlusFollower,MinusFollower,PlusCreatedAt,MinusCreatedAt,PlusUpdatedAt,MinusUpdatedAt
     */
    @Serializable
    enum class Sort(
        val value: kotlin.String,
    ) {
        @SerialName(value = "+follower")
        PlusFollower("+follower"),

        @SerialName(value = "-follower")
        MinusFollower("-follower"),

        @SerialName(value = "+createdAt")
        PlusCreatedAt("+createdAt"),

        @SerialName(value = "-createdAt")
        MinusCreatedAt("-createdAt"),

        @SerialName(value = "+updatedAt")
        PlusUpdatedAt("+updatedAt"),

        @SerialName(value = "-updatedAt")
        MinusUpdatedAt("-updatedAt"),
    }

    /**
     * *
     * Values: All,Alive
     */
    @Serializable
    enum class State(
        val value: kotlin.String,
    ) {
        @SerialName(value = "all")
        All("all"),

        @SerialName(value = "alive")
        Alive("alive"),
    }

    /**
     * *
     * Values: Combined,Local,Remote
     */
    @Serializable
    enum class Origin(
        val value: kotlin.String,
    ) {
        @SerialName(value = "combined")
        Combined("combined"),

        @SerialName(value = "local")
        Local("local"),

        @SerialName(value = "remote")
        Remote("remote"),
    }
}
