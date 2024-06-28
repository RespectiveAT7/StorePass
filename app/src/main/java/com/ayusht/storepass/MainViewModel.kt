package com.ayusht.storepass

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MainViewModel: ViewModel() {
    private val realm = MyApp.realm

    fun createHash(toBeHashed: String): ByteArray {
        val byteArray: ByteArray = toBeHashed.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(byteArray)

        return hashBytes
    }



    val passwords = realm
        .query<Passwords>()
        .asFlow()
        .map { results ->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun decrypt(algorithm: String, cipherText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }

    fun encrypt(algorithm: String, inputText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(inputText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

    fun addPassword(appNameR: String, userNameR: String, passwordR: String) {
        viewModelScope.launch {
            realm.write {
                val passwordRealm = Passwords().apply {
                    appName = appNameR
                    userName = userNameR
                    appPassword = encrypt(
                        algorithm = "AES",
                        inputText = passwordR,
                        key = SecretKeySpec(createHash("secretKey"), "AES"),
                        iv = IvParameterSpec(ByteArray(16))
                    )
                }
                if(passwordRealm.appName.isNotBlank() || passwordRealm.appPassword.isNotBlank() || passwordRealm.userName.isNotBlank()) {
                    copyToRealm(passwordRealm, UpdatePolicy.ALL)
                }
            }
        }
    }

    fun removePassword(password: Passwords) {
        viewModelScope.launch {
            realm.write {
                val latestPassword = findLatest(password) ?: return@write
                delete(latestPassword)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}

