package dev.dimension.flare.ui.presenter.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.atproto.server.CreateSessionRequest
import dev.dimension.flare.data.network.bluesky.BlueskyService
import dev.dimension.flare.data.repository.AccountRepository
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.ui.model.UiAccount
import dev.dimension.flare.ui.presenter.PresenterBase
import io.ktor.http.Url
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public class BlueskyLoginPresenter(
    private val toHome: () -> Unit,
) : PresenterBase<BlueskyLoginState>(),
    KoinComponent {
    private val accountRepository: AccountRepository by inject()

    @Composable
    override fun body(): BlueskyLoginState {
        var error by remember { mutableStateOf<Throwable?>(null) }
        val scope = rememberCoroutineScope()
        var loading by remember { mutableStateOf(false) }

        return object : BlueskyLoginState {
            override val loading = loading
            override val error = error

            override fun login(
                baseUrl: String,
                username: String,
                password: String,
            ) {
                scope.launch {
                    loading = true
                    error = null
                    runCatching {
                        blueskyLoginUseCase(
                            baseUrl = baseUrl,
                            username = username,
                            password = password,
                            accountRepository = accountRepository,
                        )
                        toHome.invoke()
                    }.onFailure {
                        error = it
                    }
                    loading = false
                }
            }
        }
    }

    private suspend fun blueskyLoginUseCase(
        baseUrl: String,
        username: String,
        password: String,
        accountRepository: AccountRepository,
    ) {
        val service = BlueskyService(baseUrl)
        val response =
            // check if username is email or custom domain
            if (username.contains("@") || username.contains(".")) {
                service.createSession(CreateSessionRequest(username, password))
            } else {
                val server = service.describeServer()
                val actualUserName =
                    server.maybeResponse()?.availableUserDomains?.firstOrNull()?.let {
                        "$username$it"
                    } ?: username
                service.createSession(CreateSessionRequest(actualUserName, password))
            }.requireResponse()
        accountRepository.addAccount(
            UiAccount.Bluesky(
                credential =
                    UiAccount.Bluesky.Credential(
                        baseUrl = baseUrl,
                        accessToken = response.accessJwt,
                        refreshToken = response.refreshJwt,
                    ),
                accountKey =
                    MicroBlogKey(
                        id = response.did.did,
                        host = Url(baseUrl).host,
                    ),
            ),
        )
    }
}

@Immutable
public interface BlueskyLoginState {
    public val loading: Boolean
    public val error: Throwable?

    public fun login(
        baseUrl: String,
        username: String,
        password: String,
    )
}
