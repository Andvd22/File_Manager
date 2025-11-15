package com.example.mylearning

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.core.app.ActivityCompat
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
        const val REQUEST_CODE_MANAGE_STORAGE = 101
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

    fun shouldShowRationale(): Boolean {
        val permissions = getRequiresPermissions()
        return permissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }

    fun isUserSaidLater(): Boolean {
        return prefs.getBoolean(KEY_USER_SAID_LATER, false)
    }

    fun setUserSaidLater(value: Boolean): Unit {
        return prefs.edit().putBoolean(KEY_USER_SAID_LATER, value).apply()
    }

    fun hasAskedPermissionBefore(): Boolean {
        return prefs.getBoolean(KEY_PERMISSIONS_ASKED, false)
    }

    fun markPermissionAsked(): Unit {
        prefs.edit().putBoolean(KEY_PERMISSIONS_ASKED, true).apply()
    }

    fun resetFlags(){
        return prefs.edit().clear().apply()
    }

    fun requestStoragePermission(){
        val permissions = getRequiresPermissions()
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_STORAGE)
        markPermissionAsked()
    }

    fun requestManageStoragePermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE)
                markPermissionAsked()
            }catch (e: Exception){
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE)
                markPermissionAsked()
            }
        }
        else{
            requestStoragePermission()
        }
    }

    fun openAppSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${activity.packageName}")
        activity.startActivityForResult(intent, REQUEST_CODE_SETTINGS)
    }

    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean{
        if(requestCode == REQUEST_CODE_STORAGE){
            if(grantResults.isNotEmpty() && grantResults.all { isGranted ->
                isGranted == PackageManager.PERMISSION_GRANTED
                })
                return true
            else{
                return false
            }
        }
        return false
    }

    fun handleActivityResult(requestCode: Int): Boolean {
        if(requestCode == REQUEST_CODE_SETTINGS || requestCode == REQUEST_CODE_MANAGE_STORAGE){
            return hasStoragePermission()
        }
        return false
    }
}