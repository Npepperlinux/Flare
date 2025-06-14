package dev.dimension.flare.ui.screen.status.action

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import dev.dimension.flare.R
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.ui.presenter.invoke
import dev.dimension.flare.ui.presenter.status.action.DeleteStatusPresenter
import dev.dimension.flare.ui.presenter.status.action.DeleteStatusState
import moe.tlaster.precompose.molecule.producePresenter

@Composable
internal fun DeleteStatusConfirmDialog(
    statusKey: MicroBlogKey,
    accountType: AccountType,
    onBack: () -> Unit,
) {
    val state by producePresenter(key = "DeleteStatusPresenter_${accountType}_$statusKey") {
        deleteStatusConfirmPresenter(
            statusKey = statusKey,
            accountType = accountType,
        )
    }

    AlertDialog(
        onDismissRequest = onBack,
        confirmButton = {
            TextButton(
                onClick = {
                    state.delete()
                    onBack.invoke()
                },
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onBack) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.delete_status_title))
        },
        text = {
            Text(text = stringResource(id = R.string.delete_status_message))
        },
    )
}

@Composable
private fun deleteStatusConfirmPresenter(
    statusKey: MicroBlogKey,
    accountType: AccountType,
) = run {
    val state =
        remember(accountType, statusKey) {
            DeleteStatusPresenter(
                accountType = accountType,
                statusKey = statusKey,
            )
        }.invoke()

    object : DeleteStatusState by state {
    }
}
