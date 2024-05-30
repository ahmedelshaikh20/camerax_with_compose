package com.example.cameraxwithcompose.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun getCameraPermission(context: Context): Boolean {
  return ContextCompat.checkSelfPermission(
    context,
    android.Manifest.permission.CAMERA
  ) == PackageManager.PERMISSION_GRANTED
}
