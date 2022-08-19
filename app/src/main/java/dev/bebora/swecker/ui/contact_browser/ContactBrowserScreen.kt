package dev.bebora.swecker.ui.contact_browser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bebora.swecker.R
import dev.bebora.swecker.data.service.impl.AccountsServiceImpl
import dev.bebora.swecker.data.service.impl.AuthServiceImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactBrowserScreen(
    modifier : Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: ContactBrowserViewModel = hiltViewModel()
) {
    val ui = viewModel.uiState
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    //TODO handle larger screen
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.contacts_title)) },
                navigationIcon = {
                    IconButton(onClick = { onGoBack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(state = rememberScrollState()),
        ) {
            ui.friends.forEachIndexed { idx, friend ->
                if (idx != 0) {
                    Divider()
                }
                ContactRow(user = friend)
            }
        }
    }
}

@Preview
@Composable
fun ContactBrowserScreenPreview() {
    ContactBrowserScreen(
        viewModel = ContactBrowserViewModel(
            authService = AuthServiceImpl(),
            accountsService = AccountsServiceImpl()
        )
    )
}

