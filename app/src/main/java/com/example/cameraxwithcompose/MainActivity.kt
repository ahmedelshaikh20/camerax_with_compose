package com.example.cameraxwithcompose

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.cameraxwithcompose.ui.screen.CameraX
import com.example.cameraxwithcompose.ui.theme.CameraXWithComposeTheme
import com.example.cameraxwithcompose.util.getCameraPermission
import com.example.cameraxwithcompose.viewmodel.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  lateinit var viewModel: CameraViewModel



  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val cameraPermission = getCameraPermission(this)
    setContent {
      CameraXWithComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          CameraX(cameraPermission)

        }
      }
    }
  }


}


