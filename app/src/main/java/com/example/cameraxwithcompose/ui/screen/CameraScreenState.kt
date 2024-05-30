package com.example.cameraxwithcompose.ui.screen

import androidx.camera.core.CameraSelector

data class CameraScreenState(
  val selectedCamera : CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
)
