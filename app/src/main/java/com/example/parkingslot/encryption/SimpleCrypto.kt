package com.example.parkingslot.encryption

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SimpleCrypto {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private val secretKey: SecretKey = SecretKeySpec("myEncryptionKey1".toByteArray(), "AES") // must be 16 chars
    private val iv = IvParameterSpec("RandomInitVector".toByteArray()) // must be 16 chars

    fun encrypt(input: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(input: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val decodedBytes = Base64.decode(input, Base64.DEFAULT)
        val decrypted = cipher.doFinal(decodedBytes)
        return String(decrypted, Charsets.UTF_8)
    }
}
