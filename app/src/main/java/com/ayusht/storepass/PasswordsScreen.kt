package com.ayusht.storepass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalStdlibApi::class
)
@Composable
fun PasswordsScreen(
    viewModel: MainViewModel,
    passwordsList: List<Passwords> = emptyList()
) {
    var appName by remember { mutableStateOf("") }
    var appPassword by remember { mutableStateOf("") }
    var appUserName by remember { mutableStateOf("") }
    var addDialogHidden by remember { mutableStateOf(true) }
    var infoAlertHidden by remember { mutableStateOf(true) }

    var passwordItem: Passwords = Passwords()


    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Heading icon",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(text = "Password Manager", style = MaterialTheme.typography.titleLarge)
                    }

                    Text(text = "All passwords are encrypted on storage", style = MaterialTheme.typography.titleSmall)
                }
            }, modifier = Modifier.shadow(24.dp, spotColor = MaterialTheme.colorScheme.inverseSurface))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                addDialogHidden = false
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Password")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            if(!addDialogHidden) {
                ModalBottomSheet(onDismissRequest = { addDialogHidden = !addDialogHidden }) {
                    AddDataDialog(
                        iAppName = appName,
                        iAppPassword = appPassword,
                        iAppUserName = appUserName,
                        changeAppName = { text -> appName = text },
                        changeAppPass = { text -> appPassword = text },
                        changeUserName = { text -> appUserName = text },
                        handleClick = {
                            viewModel.addPassword(
                                appNameR = appName,
                                passwordR = appPassword,
                                userNameR = appUserName
                            )
                            appName = ""
                            appPassword = ""
                            appUserName = ""
                            keyboardController?.hide()
                            addDialogHidden = true
                        }
                    )
                }
            }

            if(!infoAlertHidden) {
                UpdatePassword(
                    handleDismiss = { infoAlertHidden = !infoAlertHidden },
                    passItem = passwordItem,
                    decrypt = { text -> viewModel.decrypt(
                        algorithm = "AES",
                        cipherText = text,
                        key = SecretKeySpec(viewModel.createHash("secretKey"), "AES"),
                        iv = IvParameterSpec(ByteArray(16))
                    )}
                )
            }

            LazyColumn {
                item {
                    passwordsList.forEach{
                        PasswordCard(
                            passItem = it,
                            modifier = Modifier.fillMaxWidth(),
                            handleDelete = { password -> viewModel.removePassword(password) },
                            handleInfo = {passItem ->
                                infoAlertHidden = false
                                passwordItem = passItem
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordCard(
    modifier: Modifier = Modifier,
    passItem: Passwords,
    handleDelete: (Passwords) -> Unit,
    handleInfo: (Passwords) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .padding(12.dp)
            .size(100.dp),
        onClick = { handleInfo(passItem) }
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = passItem.appName, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { handleDelete(passItem) }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Password")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePassword(
    handleDismiss: () -> Unit,
    passItem: Passwords,
    decrypt: (String) -> String
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = { handleDismiss() },
        content = {
            ElevatedCard(
                modifier = Modifier
                    .padding(24.dp)
                    .size(100.dp, 240.dp)
            ) {
                Text(text = passItem.appName, style = MaterialTheme.typography.titleMedium)
                Text(text = decrypt(passItem.appPassword), style = MaterialTheme.typography.titleMedium)
                Text(text = passItem.userName, style = MaterialTheme.typography.titleMedium)
            }
        }
    )
}
