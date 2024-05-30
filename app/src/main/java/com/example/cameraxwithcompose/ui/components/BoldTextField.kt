package com.example.cameraxwithcompose.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.cameraxwithcompose.R

@Composable
fun BoldTextField(
  value: String,
  size: TextUnit,
  modifier: Modifier = Modifier,
  textAlign: TextAlign
) {
  Text(
    text = value,
    modifier = modifier
      .heightIn(),
    fontStyle = FontStyle.Normal,
    fontSize = size,
    overflow = TextOverflow.Clip,
    maxLines = 1,
    fontWeight = FontWeight.Bold,
    color = colorResource(id = R.color.white),
    textAlign = textAlign,

  )
}
