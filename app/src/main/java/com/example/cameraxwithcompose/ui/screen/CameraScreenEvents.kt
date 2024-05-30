package com.example.cameraxwithcompose.ui.screen

import android.content.Context
import androidx.camera.core.ImageCapture

sealed class CameraScreenEvents {

  object onSwitchCameraClick : CameraScreenEvents()
  data class onTakePhotoClick(val imageCapture: ImageCapture, val context: Context) : CameraScreenEvents()



}
