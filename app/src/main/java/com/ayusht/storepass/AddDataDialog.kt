package com.ayusht.storepass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddDataDialog(
    modifier: Modifier = Modifier,
    iAppName: String,
    iAppPassword: String,
    iAppUserName: String,
    changeAppName: (String) -> Unit,
    changeAppPass: (String) -> Unit,
    changeUserName: (String) -> Unit,
    handleClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = iAppName,
            onValueChange = { value -> changeAppName(value) },
            placeholder = { Text(text = "Enter App Name") },
            label = { Text(text = "App Name") },
            modifier = modifier
                .padding(4.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = iAppUserName,
            onValueChange = { value -> changeUserName(value) },
            placeholder = { Text(text = "Enter Username/Email") },
            label = { Text(text = "Username/Email") },
            modifier = modifier
                .padding(4.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = iAppPassword,
            onValueChange = { value -> changeAppPass(value) },
            placeholder = { Text(text = "Enter App Password") },
            label = { Text(text = "App Password") },

            modifier = modifier
                .padding(4.dp)
                .fillMaxWidth()
        )

        fun isEnabled(): Boolean {
            return !(iAppName.isBlank() || iAppPassword.isBlank() || iAppUserName.isBlank())
        }
        ElevatedButton(onClick = { handleClick() }, modifier = modifier.padding(12.dp), enabled = isEnabled()) {
            Text(text = "Save Password")
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}