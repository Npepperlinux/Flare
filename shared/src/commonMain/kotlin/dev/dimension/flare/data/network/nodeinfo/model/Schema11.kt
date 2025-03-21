package dev.dimension.flare.data.network.nodeinfo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * NodeInfo schema version 1.1.
 */
@Serializable
internal data class Schema11(
    /**
     * Free form key value pairs for software specific values. Clients should not rely on any
     * specific key present.
     */
    val metadata: JsonObject? = null,
    /**
     * Whether this server allows open self-registration.
     */
    val openRegistrations: Boolean? = null,
    /**
     * The protocols supported on this server.
     */
    val protocols: Protocols? = null,
    /**
     * The third party sites this server can connect to via their application API.
     */
    val services: Services? = null,
    /**
     * Metadata about server software in use.
     */
    val software: Software? = null,
    /**
     * Usage statistics for this server.
     */
    val usage: Usage? = null,
    /**
     * The schema version, must be 1.1.
     */
    val version: Version? = null,
) {
    /**
     * The protocols supported on this server.
     */
    @Serializable
    data class Protocols(
        /**
         * The protocols this server can receive traffic for.
         */
        val inbound: List<Bound>? = null,
        /**
         * The protocols this server can generate traffic for.
         */
        val outbound: List<Bound>? = null,
    )

    @Serializable
    enum class Bound(
        val value: String,
    ) {
        @SerialName("buddycloud")
        Buddycloud("buddycloud"),

        @SerialName("diaspora")
        Diaspora("diaspora"),

        @SerialName("friendica")
        Friendica("friendica"),

        @SerialName("gnusocial")
        Gnusocial("gnusocial"),

        @SerialName("libertree")
        Libertree("libertree"),

        @SerialName("mediagoblin")
        Mediagoblin("mediagoblin"),

        @SerialName("pumpio")
        Pumpio("pumpio"),

        @SerialName("redmatrix")
        Redmatrix("redmatrix"),

        @SerialName("smtp")
        SMTP("smtp"),

        @SerialName("tent")
        Tent("tent"),

        @SerialName("zot")
        Zot("zot"),
    }

    /**
     * The third party sites this server can connect to via their application API.
     */
    @Serializable
    data class Services(
        /**
         * The third party sites this server can retrieve messages from for combined display with
         * regular traffic.
         */
        val inbound: List<Inbound>? = null,
        /**
         * The third party sites this server can publish messages to on the behalf of a user.
         */
        val outbound: List<Outbound>? = null,
    )

    @Serializable
    enum class Inbound(
        val value: String,
    ) {
        @SerialName("appnet")
        Appnet("appnet"),

        @SerialName("gnusocial")
        Gnusocial("gnusocial"),

        @SerialName("pumpio")
        Pumpio("pumpio"),
    }

    @Serializable
    enum class Outbound(
        val value: String,
    ) {
        @SerialName("appnet")
        Appnet("appnet"),

        @SerialName("blogger")
        Blogger("blogger"),

        @SerialName("buddycloud")
        Buddycloud("buddycloud"),

        @SerialName("diaspora")
        Diaspora("diaspora"),

        @SerialName("dreamwidth")
        Dreamwidth("dreamwidth"),

        @SerialName("drupal")
        Drupal("drupal"),

        @SerialName("facebook")
        Facebook("facebook"),

        @SerialName("friendica")
        Friendica("friendica"),

        @SerialName("gnusocial")
        Gnusocial("gnusocial"),

        @SerialName("google")
        Google("google"),

        @SerialName("insanejournal")
        Insanejournal("insanejournal"),

        @SerialName("libertree")
        Libertree("libertree"),

        @SerialName("linkedin")
        Linkedin("linkedin"),

        @SerialName("livejournal")
        Livejournal("livejournal"),

        @SerialName("mediagoblin")
        Mediagoblin("mediagoblin"),

        @SerialName("myspace")
        Myspace("myspace"),

        @SerialName("pinterest")
        Pinterest("pinterest"),

        @SerialName("posterous")
        Posterous("posterous"),

        @SerialName("pumpio")
        Pumpio("pumpio"),

        @SerialName("redmatrix")
        Redmatrix("redmatrix"),

        @SerialName("smtp")
        SMTP("smtp"),

        @SerialName("tent")
        Tent("tent"),

        @SerialName("tumblr")
        Tumblr("tumblr"),

        @SerialName("twitter")
        Twitter("twitter"),

        @SerialName("wordpress")
        Wordpress("wordpress"),

        @SerialName("xmpp")
        XMPP("xmpp"),
    }

    /**
     * Metadata about server software in use.
     */
    @Serializable
    data class Software(
        /**
         * The canonical name of this server software.
         */
        val name: Name? = null,
        /**
         * The version of this server software.
         */
        val version: String? = null,
    )

    /**
     * The canonical name of this server software.
     */
    @Serializable
    enum class Name(
        val value: String,
    ) {
        @SerialName("diaspora")
        Diaspora("diaspora"),

        @SerialName("friendica")
        Friendica("friendica"),

        @SerialName("hubzilla")
        Hubzilla("hubzilla"),

        @SerialName("redmatrix")
        Redmatrix("redmatrix"),
    }

    /**
     * Usage statistics for this server.
     */
    @Serializable
    data class Usage(
        /**
         * The amount of comments that were made by users that are registered on this server.
         */
        val localComments: Long? = null,
        /**
         * The amount of posts that were made by users that are registered on this server.
         */
        val localPosts: Long? = null,
        /**
         * statistics about the users of this server.
         */
        val users: Users? = null,
    )

    /**
     * statistics about the users of this server.
     */
    @Serializable
    data class Users(
        /**
         * The amount of users that signed in at least once in the last 180 days.
         */
        val activeHalfyear: Long? = null,
        /**
         * The amount of users that signed in at least once in the last 30 days.
         */
        val activeMonth: Long? = null,
        /**
         * The total amount of on this server registered users.
         */
        val total: Long? = null,
    )

    /**
     * The schema version, must be 1.1.
     */
    @Serializable
    enum class Version(
        val value: String,
    ) {
        @SerialName("1.1")
        The11("1.1"),
    }
}
