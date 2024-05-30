package com.example.cameraxwithcompose.ui.screen

import android.content.Context
import androidx.camera.core.ImageCapture

sealed class CameraScreenEvents {

  object OnSwitchCameraClick : CameraScreenEvents()
  data class OnTakePhotoClick(val imageCapture: ImageCapture, val context: Context) : CameraScreenEvents()



}
