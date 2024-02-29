package com.shubham.detect_pose

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.shubham.detect_pose.ui.theme.VideoPlayer
import java.util.concurrent.Executor

//@Preview
@Composable
fun YogaScreen(poseLandmarker: PoseLandmarker, executor: Executor, poseResult: MutableState<PoseDetector.ResultBundle?>){
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)) {
        Box (
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
//            contentAlignment = Alignment.Center,
        ){
            val dropdownMenuHelper = remember { ExposedDropdownMenuHelper() }
            PoseDetectionPreview(poseLandmarker, executor, poseResult)
            dropdownMenuHelper.ExposedDropdownMenuBox(context)
        }

        Box (
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Cyan),
            contentAlignment = Alignment.Center,

            ){
            val videoResId = R.raw.video4
            VideoPlayer(videoResId)

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
class ExposedDropdownMenuHelper {

    @Composable
    fun ExposedDropdownMenuBox(context: Context) {
        var expanded by remember { mutableStateOf(false) }
        val modelNames = listOf("warrior2","warrior2left")
        var selectedModel by remember {mutableStateOf(modelNames[0]) }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box {
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        value = selectedModel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .width(150.dp)

                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        modelNames.forEach { modelName ->
                            DropdownMenuItem(
                                text = { Text(text = modelName) },
                                onClick = {
                                    selectedModel = modelName
                                    expanded = false
                                    Toast.makeText(context, modelName, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
        Log.d("modsel",selectedModel)

    }
}
object GlobalModelValues {
    var ModelName: String by mutableStateOf("warrior2")
}
