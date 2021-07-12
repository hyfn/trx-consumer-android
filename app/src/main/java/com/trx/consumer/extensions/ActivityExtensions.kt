package com.trx.consumer.extensions

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

//  TODO: Will be replaced by PermissionManager once complete, Make sure to refactor. 
fun AppCompatActivity.checkLivePermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
