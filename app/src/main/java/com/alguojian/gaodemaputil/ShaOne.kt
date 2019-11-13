package com.alguojian.gaodemaputil

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

object ShaOne {

    fun sha1(context: Context): String? {
        try {
            val info = context.packageManager.getPackageInfo(
                    context.packageName, PackageManager.GET_SIGNATURES)
            val cert = info.signatures[0].toByteArray()
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(cert)
            val hexString = StringBuffer()
            for (i in publicKey.indices) {
                val appendString = Integer.toHexString(0xFF and publicKey[i].toInt())
                        .toUpperCase(Locale.US)
                if (appendString.length == 1)
                    hexString.append("0")
                hexString.append(appendString)
                hexString.append(":")
            }
            val result = hexString.toString()
            return result.substring(0, result.length - 1)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return null
    }

}
