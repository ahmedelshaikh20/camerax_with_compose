package com.example.cameraxwithcompose.ui.screen

import android.content.Context
import android.graphics.PointF
import android.hardware.display.DisplayManager
import android.util.Log
import android.view.Display
import android.view.Surface
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cameraxwithcompose.ml.FaceDetectionAnalyzer
import com.example.cameraxwithcompose.ui.components.BasicButton
import com.example.cameraxwithcompose.util.drawBounds
import com.example.cameraxwithcompose.viewmodel.CameraViewModel
import com.google.mlkit.vision.face.Face

@Composable
fun CameraX(cameraPermission: Boolean, viewModel: CameraViewModel = hiltViewModel()) {
  val state = viewModel.state
  val localContext = LocalContext.current
  val displayManager = localContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
  val rotation = displayManager.getDisplay(Display.DEFAULT_DISPLAY)?.rotation ?: Surface.ROTATION_0
  val imageCapture = remember {
    ImageCapture.Builder()
      .setTargetRotation(rotation)
      .build()
  }
  var isCameraGranted by remember { mutableStateOf(cameraPermission) }
  val launcher =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
      if (isGranted) {
        Log.d("Camera is Granted", "$isCameraGranted")
        isCameraGranted = true
      } else {
        // Request The permission
        Log.d("Camera is Granted", "$isCameraGranted")
      }
    }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {


    AnimatedVisibility(visible = isCameraGranted) {
      CameraContent(
        modifier = Modifier,
        state.selectedCamera,
        onSwitchClick = { viewModel.onEvent(CameraScreenEvents.OnSwitchCameraClick) },
        onTakePhotoClick = {
          viewModel.onEvent(
            CameraScreenEvents.OnTakePhotoClick(
              imageCapture,
              localContext
            )
          )
        },
        imageCapture = imageCapture,
        context = localContext
      )
    }

    BasicButton(modifier = Modifier
      .heightIn(), value = "Open Camera", onClick = {
      if (!isCameraGranted) {
        launcher.launch(android.Manifest.permission.CAMERA)
      }
    })
  }


}

@Composable
fun CameraContent(
  modifier: Modifier = Modifier,
  selectedCamera: CameraSelector,
  imageCapture: ImageCapture,
  onSwitchClick: () -> Unit,
  onTakePhotoClick: () -> Unit,
  context: Context
) {

  val lifecycleOwner = LocalLifecycleOwner.current
  val faces = remember { mutableStateListOf<Face>() }
  val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
  val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }
  val imageWidth = remember { mutableStateOf(0) }
  val imageHeight = remember { mutableStateOf(0) }

  val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }


  val previewView = remember { PreviewView(context) }
  val executor = ContextCompat.getMainExecutor(context)
  LaunchedEffect(selectedCamera) {
    Log.d("Camera Selection ", selectedCamera.toString())
    val cameraProvider = cameraProviderFuture.get()

    val preview = Preview.Builder().build().apply {
      setSurfaceProvider(previewView.surfaceProvider)
    }
    // This anlayzer where the images will be through to detect faces in
    val imageAnalysis = ImageAnalysis.Builder()
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
      .build().apply {
        setAnalyzer(executor, FaceDetectionAnalyzer { listFaces, width, height ->
          faces.clear()
          faces.addAll(listFaces)
          imageWidth.value = width
          imageHeight.value = height

        })
      }

    try {
      cameraProvider.unbindAll()
      //We bind Camera provider to the current lifecycle with usecases
      //image capture for capturing image
      cameraProvider.bindToLifecycle(
        lifecycleOwner,
        selectedCamera,
        imageCapture,
        preview,
        imageAnalysis
      )
    } catch (exc: Exception) {
      Log.e("CameraX", "Use case binding failed", exc)
    }
  }
  Box(modifier = Modifier.fillMaxSize()) {

    Scaffold(
      modifier = modifier.fillMaxSize(),

      ) {
      AndroidView(factory = { context ->
        FrameLayout(context).apply {
          layoutParams = ViewGroup.LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT
          )
          addView(previewView)
        }
      })
    }
    BasicButton(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(5.dp),
      value = "Take Photo",
      onClick = {
        onTakePhotoClick()
      }
    )

    BasicButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(5.dp),
      value = "Switch the Camera",
      onClick = {
        Log.d("Camera Selection", "Camera Clicked")
        onSwitchClick()
      }
    )
  }
  DrawFaces(
    faces = faces,
    imageHeight.value,
    imageWidth.value,
    screenWidth.value,
    screenHeight.value
  )


}


@Composable
fun DrawFaces(
  faces: List<Face>,
  imageWidth: Int,
  imageHeight: Int,
  screenWidth: Int,
  screenHeight: Int
) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val scaleX = screenWidth / imageWidth.toFloat()
    val scaleY = screenHeight / imageHeight.toFloat()

    faces.forEach { face ->
      val boundingBox = face.boundingBox
      val topLeft = PointF(boundingBox.left * scaleX, boundingBox.top * scaleY)
      val size = Size(boundingBox.width() * scaleX, boundingBox.height() * scaleY)

      drawBounds(topLeft, size, Color.Yellow, 10f)
    }
  }
}
