package dev.dimension.flare.data.network.misskey.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import dev.dimension.flare.data.network.misskey.api.model.Hashtag
import dev.dimension.flare.data.network.misskey.api.model.HashtagsListRequest
import dev.dimension.flare.data.network.misskey.api.model.HashtagsSearchRequest
import dev.dimension.flare.data.network.misskey.api.model.HashtagsShowRequest
import dev.dimension.flare.data.network.misskey.api.model.HashtagsTrend200ResponseInner
import dev.dimension.flare.data.network.misskey.api.model.HashtagsUsersRequest
import dev.dimension.flare.data.network.misskey.api.model.User

internal interface HashtagsApi {
    /**
     * hashtags/list
     * No description provided.  **Credential required**: *No*
     * Responses:
     *  - 200: OK (with results)
     *  - 400: Client error
     *  - 401: Authentication error
     *  - 403: Forbidden error
     *  - 418: I'm Ai
     *  - 500: Internal server error
     *
     * @param hashtagsListRequest * @return [kotlin.collections.List<Hashtag>]
     */
    @POST("hashtags/list")
    suspend fun hashtagsList(
        @Body hashtagsListRequest: HashtagsListRequest,
    ): kotlin.collections.List<Hashtag>

    /**
     * hashtags/search
     * No description provided.  **Credential required**: *No*
     * Responses:
     *  - 200: OK (with results)
     *  - 400: Client error
     *  - 401: Authentication error
     *  - 403: Forbidden error
     *  - 418: I'm Ai
     *  - 500: Internal server error
     *
     * @param hashtagsSearchRequest * @return [kotlin.collections.List<kotlin.String>]
     */
    @POST("hashtags/search")
    suspend fun hashtagsSearch(
        @Body hashtagsSearchRequest: HashtagsSearchRequest,
    ): kotlin.collections.List<kotlin.String>

    /**
     * hashtags/show
     * No description provided.  **Credential required**: *No*
     * Responses:
     *  - 200: OK (with results)
     *  - 400: Client error
     *  - 401: Authentication error
     *  - 403: Forbidden error
     *  - 418: I'm Ai
     *  - 500: Internal server error
     *
     * @param hashtagsShowRequest * @return [Hashtag]
     */
    @POST("hashtags/show")
    suspend fun hashtagsShow(
        @Body hashtagsShowRequest: HashtagsShowRequest,
    ): Hashtag

    /**
     * hashtags/trend
     * No description provided.  **Credential required**: *No*
     * Responses:
     *  - 200: OK (with results)
     *  - 400: Client error
     *  - 401: Authentication error
     *  - 403: Forbidden error
     *  - 418: I'm Ai
     *  - 500: Internal server error
     *
     * @param body * @return [kotlin.collections.List<HashtagsTrend200ResponseInner>]
     */
    @GET("hashtags/trend")
    suspend fun hashtagsTrend(): kotlin.collections.List<HashtagsTrend200ResponseInner>

    /**
     * hashtags/users
     * No description provided.  **Credential required**: *No*
     * Responses:
     *  - 200: OK (with results)
     *  - 400: Client error
     *  - 401: Authentication error
     *  - 403: Forbidden error
     *  - 418: I'm Ai
     *  - 500: Internal server error
     *
     * @param hashtagsUsersRequest * @return [kotlin.collections.List<UserDetailed>]
     */
    @POST("hashtags/users")
    suspend fun hashtagsUsers(
        @Body hashtagsUsersRequest: HashtagsUsersRequest,
    ): kotlin.collections.List<User>
}
