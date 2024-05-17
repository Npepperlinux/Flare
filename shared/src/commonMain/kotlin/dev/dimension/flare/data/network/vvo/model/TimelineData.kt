package dev.dimension.flare.data.network.vvo.model

import kotlinx.datetime.Instant
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray

@Serializable
data class VVOResponse<T>(
    val data: T? = null,
    val ok: Long? = null,
    @SerialName("http_code")
    val httpCode: Long? = null,
)

@Serializable
data class TimelineData(
    val statuses: List<Status>? = null,
    val advertises: JsonArray? = null,
    val ad: JsonArray? = null,
    @SerialName("filtered_ids")
    val filteredIDS: JsonArray? = null,
    val hasvisible: Boolean? = null,
    @SerialName("previous_cursor")
    val previousCursor: Long? = null,
    @SerialName("next_cursor")
    val nextCursor: Long? = null,
    @SerialName("previous_cursor_str")
    val previousCursorStr: String? = null,
    @SerialName("next_cursor_str")
    val nextCursorStr: String? = null,
    @SerialName("total_number")
    val totalNumber: Long? = null,
    val interval: Long? = null,
    @SerialName("uve_blank")
    val uveBlank: Long? = null,
    @SerialName("since_id")
    val sinceID: Long? = null,
    @SerialName("since_id_str")
    val sinceIDStr: String? = null,
    @SerialName("max_id")
    val maxID: Long? = null,
    @SerialName("max_id_str")
    val maxIDStr: String? = null,
    @SerialName("has_unread")
    val hasUnread: Long? = null,
)

@Serializable
data class Status(
    val visible: Visible? = null,
    @SerialName("created_at")
    @Serializable(with = VVODateSerializer::class)
    val createdAt: Instant? = null,
    val id: String,
    val mid: String? = null,
    @SerialName("can_edit")
    val canEdit: Boolean? = null,
    @SerialName("show_additional_indication")
    val showAdditionalIndication: Long? = null,
    val text: String? = null,
    val source: String? = null,
    val favorited: Boolean? = null,
    @SerialName("pic_ids")
    val picIDS: List<String>? = null,
    @SerialName("is_paid")
    val isPaid: Boolean? = null,
    @SerialName("mblog_vip_type")
    val mblogVipType: Long? = null,
    val user: User? = null,
    val pid: Long? = null,
    val pidstr: String? = null,
    @SerialName("retweeted_status")
    val retweetedStatus: Status? = null,
    @SerialName("reposts_count")
    val repostsCount: Long? = null,
    @SerialName("comments_count")
    val commentsCount: Long? = null,
    @SerialName("reprint_cmt_count")
    val reprintCmtCount: Long? = null,
    @SerialName("attitudes_count")
    val attitudesCount: Long? = null,
    @SerialName("pending_approval_count")
    val pendingApprovalCount: Long? = null,
    val isLongText: Boolean? = null,
    @SerialName("show_mlevel")
    val showMlevel: Long? = null,
    @SerialName("darwin_tags")
    val darwinTags: JsonArray? = null,
    @SerialName("ad_marked")
    val adMarked: Boolean? = null,
    val mblogtype: Long? = null,
    @SerialName("item_category")
    val itemCategory: String? = null,
    val rid: String? = null,
    val mlevelSource: String? = null,
    val cardid: String? = null,
    @SerialName("number_display_strategy")
    val numberDisplayStrategy: NumberDisplayStrategy? = null,
    @SerialName("content_auth")
    val contentAuth: Long? = null,
    @SerialName("comment_manage_info")
    val commentManageInfo: CommentManageInfo? = null,
    @SerialName("repost_type")
    val repostType: Long? = null,
    @SerialName("pic_num")
    val picNum: Long? = null,
    @SerialName("hot_page")
    val hotPage: HotPage? = null,
    @SerialName("new_comment_style")
    val newCommentStyle: Long? = null,
    @SerialName("ab_switcher")
    val abSwitcher: Long? = null,
    val mlevel: Long? = null,
    @SerialName("region_name")
    val regionName: String? = null,
    @SerialName("region_opt")
    val regionOpt: Long? = null,
    @SerialName("raw_text")
    val rawText: String? = null,
    val bid: String? = null,
    val textLength: Long? = null,
    @SerialName("thumbnail_pic")
    val thumbnailPic: String? = null,
    @SerialName("bmiddle_pic")
    val bmiddlePic: String? = null,
    @SerialName("original_pic")
    val originalPic: String? = null,
    @SerialName("can_remark")
    val canRemark: Boolean? = null,
    @SerialName("safe_tags")
    val safeTags: Long? = null,
    val pics: List<StatusPic>? = null,
    val picStatus: String? = null,
    @SerialName("attitude_dynamic_members_message")
    val attitudeDynamicMembersMessage: AttitudeDynamicMembersMessage? = null,
    @SerialName("page_info")
    val pageInfo: StatusPageInfo? = null,
)

@Serializable
data class AttitudeDynamicMembersMessage(
    @SerialName("user_grace_setting")
    val userGraceSetting: UserGraceSetting? = null,
    val bgimg: String? = null,
    val colorT: String? = null,
    val portrait: String? = null,
    @SerialName("media_url")
    val mediaURL: String? = null,
    @SerialName("default_media_url")
    val defaultMediaURL: String? = null,
    @SerialName("media_id")
    val mediaID: String? = null,
    val protocol: String? = null,
    @SerialName("scene_show_option")
    val sceneShowOption: Long? = null,
    val scheme: String? = null,
    val type: Long? = null,
)

@Serializable
data class UserGraceSetting(
    @SerialName("zh_CN")
    val zhCN: String? = null,
    @SerialName("zh_TW")
    val zhTW: String? = null,
    @SerialName("en_US")
    val enUS: String? = null,
)

@Serializable
data class CommentManageInfo(
    @SerialName("comment_permission_type")
    val commentPermissionType: Long? = null,
    @SerialName("approval_comment_type")
    val approvalCommentType: Long? = null,
    @SerialName("comment_sort_type")
    val commentSortType: Long? = null,
    @SerialName("ai_play_picture_type")
    val aiPlayPictureType: Long? = null,
)

@Serializable
data class HotPage(
    val fid: String? = null,
    @SerialName("feed_detail_type")
    val feedDetailType: Long? = null,
)

@Serializable
data class NumberDisplayStrategy(
    @SerialName("apply_scenario_flag")
    val applyScenarioFlag: Long? = null,
    @SerialName("display_text_min_number")
    val displayTextMinNumber: Long? = null,
    @SerialName("display_text")
    val displayText: String? = null,
)

@Serializable
data class StatusPageInfo(
    val type: String? = null,
    @SerialName("object_type")
    val objectType: Long? = null,
    @SerialName("page_pic")
    val pagePic: PurplePagePic? = null,
    @SerialName("page_url")
    val pageURL: String? = null,
    @SerialName("page_title")
    val pageTitle: String? = null,
    val content1: String? = null,
    @SerialName("url_ori")
    val urlOri: String? = null,
)

@Serializable
data class PurplePagePic(
    val url: String? = null,
)

@Serializable
data class StatusPic(
    val pid: String? = null,
    val url: String? = null,
    val size: String? = null,
    val geo: StatusPicGeo? = null,
    val large: Large? = null,
)

@Serializable
data class Large(
    val size: String? = null,
    val url: String? = null,
    val geo: StatusPicGeo? = null,
)

@Serializable
data class StatusPicGeo(
    val width: Height? = null,
    val height: Height? = null,
    val croped: Boolean? = null,
) {
    val widthValue: Float
        get() =
            when (width) {
                is Height.IntegerValue -> width.value
                is Height.StringValue -> width.value.toFloatOrNull() ?: 0f
                null -> 0f
            }

    val heightValue: Float
        get() =
            when (height) {
                is Height.IntegerValue -> height.value
                is Height.StringValue -> height.value.toFloatOrNull() ?: 0f
                null -> 0f
            }
}

@Serializable
sealed class Height {
    class IntegerValue(val value: Float) : Height()

    class StringValue(val value: String) : Height()
}

@Serializable
data class FocusPoint(
    val left: Double? = null,
    val top: Double? = null,
    val width: Double? = null,
    val height: Double? = null,
    val type: Long? = null,
)

@Serializable
data class User(
    val id: Long,
    @SerialName("screen_name")
    val screenName: String,
    @SerialName("profile_image_url")
    val profileImageURL: String? = null,
    @SerialName("profile_url")
    val profileURL: String? = null,
    @SerialName("statuses_count")
    val statusesCount: Long? = null,
    val verified: Boolean? = null,
    @SerialName("verified_type")
    val verifiedType: Long? = null,
    @SerialName("verified_type_ext")
    val verifiedTypeEXT: Long? = null,
    @SerialName("verified_reason")
    val verifiedReason: String? = null,
    @SerialName("close_blue_v")
    val closeBlueV: Boolean? = null,
    val description: String? = null,
    val gender: String? = null,
    val mbtype: Long? = null,
    val svip: Long? = null,
    val urank: Long? = null,
    val mbrank: Long? = null,
    @SerialName("follow_me")
    val followMe: Boolean? = null,
    val following: Boolean? = null,
    @SerialName("follow_count")
    val followCount: Long? = null,
    @SerialName("followers_count")
    val followersCount: String? = null,
    @SerialName("followers_count_str")
    val followersCountStr: String? = null,
    @SerialName("cover_image_phone")
    val coverImagePhone: String? = null,
    @SerialName("avatar_hd")
    val avatarHD: String? = null,
    val like: Boolean? = null,
    @SerialName("like_me")
    val likeMe: Boolean? = null,
    val badge: Map<String, Long>? = null,
    @SerialName("special_follow")
    val specialFollow: Boolean? = null,
)

@Serializable
data class Visible(
    val type: Long? = null,
    @SerialName("list_id")
    val listID: Long? = null,
)

internal object VVODateSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val str = decoder.decodeString()
        return Instant.parse(
            str,
            format =
                Format {
                    // EEE MMM dd HH:mm:ss Z yyyy
                    dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
                    char(' ')
                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                    char(' ')
                    dayOfMonth(Padding.NONE)
                    char(' ')
                    hour()
                    char(':')
                    minute()
                    char(':')
                    second()
                    char(' ')
                    offset(UtcOffset.Formats.FOUR_DIGITS)
                    char(' ')
                    year()
                },
        )
    }

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeString(value.toString())
    }
}
