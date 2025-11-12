package com.example.mylearning

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat

class PermissionHelper (
    private val activity: Activity
){
    private val prefs: SharedPreferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "file_manager_prefs"
        private const val KEY_PERMISSIONS_ASKED = "permissions_asked"
        private const val KEY_USER_SAID_LATER = "user_said_later"

        const val  REQUEST_CODE_STORAGE = 100
        const val REQUEST_CODE_MANAGE_STORE = 101
        const val REQUEST_CODE_SETTINGS = 102

        private fun getRequiresPermissions(): Array<String>{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            }
            else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else{
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    fun hasStoragePermission(): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(Environment.isExternalStorageManager()){
                return true
            }
        }
        val permissions = getRequiresPermissions()
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity,permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

}