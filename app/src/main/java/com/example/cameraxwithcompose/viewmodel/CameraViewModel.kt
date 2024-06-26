package com.example.cameraxwithcompose.viewmodel

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cameraxwithcompose.ui.screen.CameraScreenEvents
import com.example.cameraxwithcompose.ui.screen.CameraScreenState
import com.example.cameraxwithcompose.util.CameraFileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

  var state by mutableStateOf(CameraScreenState())

  fun onEvent(event: CameraScreenEvents) {
    when (event) {
      CameraScreenEvents.OnSwitchCameraClick -> {
        state =
          state.copy(selectedCamera = if (state.selectedCamera == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA)
      }

      is CameraScreenEvents.OnTakePhotoClick -> {
        CameraFileUtils.takePicture(event.imageCapture, event.context)
      }
    }

  }
}
