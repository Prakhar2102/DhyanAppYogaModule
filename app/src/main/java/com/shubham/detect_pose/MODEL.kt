package com.shubham.detect_pose

import android.content.res.AssetFileDescriptor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


@Composable
fun processResultBundle(resultBundle: MutableState<PoseDetector.ResultBundle?>){
    fun preprocessData(inputLists: List<List<String>>): Array<Array<FloatArray>> {
        val reshapedArray = Array(1) { Array(30) { FloatArray(165) } }


        inputLists.forEachIndexed { i, coordinateList ->
            coordinateList.forEachIndexed { j, coordinate ->
                reshapedArray[0][i][j] = coordinate.toFloat()
            }
        }
        return reshapedArray
    }

    val output = Array(1) { FloatArray(1) }
    val resultBundl = resultBundle.value
    val resultList = mutableListOf<List<List<String>>>()

    val thirty = mutableListOf<List<String>>()
    var frames = 0
    while (frames<30){
        resultBundl?.results?.forEach { result ->


            result.landmarks().forEach { poseLandmarks ->
                val tempList = mutableListOf<String>()
                if (poseLandmarks.isNotEmpty()) { // Check if landmarks are not empty
                    poseLandmarks.forEach { landmark ->
                        tempList.add("${landmark.x()}")
                        tempList.add("${landmark.y()}")
                        tempList.add("${landmark.z()}")
                        val presence = landmark.presence().toString().removePrefix("Optional[").removeSuffix("]")
                        val visibility = landmark.visibility().toString().removePrefix("Optional[").removeSuffix("]")
                        tempList.add(presence)
                        tempList.add(visibility)
                    }
                }
                if (tempList.size!=0){
//            Log.d("temp list",tempList.toString())
//            Log.d("temp size",tempList.size.toString())
                    thirty.add(tempList)


                }
            }

        }
        frames++
    }
//    Log.d("thirty size",thirty.size.toString())
//    Log.d("thirty",thirty.toString())
    if (thirty.size == 30) {
        resultList.add(thirty)
    }
//    Log.d("resultlist",resultList.size.toString())

    if (resultList.size!=0) {
        resultList.forEach { inputLists ->

            val floatArray = preprocessData(inputLists)
            val tflite = Interpreter(loadModelFile()!!)
            tflite.run(floatArray, output)
        }
    }
    val a = output.contentDeepToString() // Convert the array to a string representation
    display(a = a)
}

@Composable
fun display(a:String){
    Box(
        Modifier
            .height(50.dp)
            .width(100.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ){
        Text(text = a)
    }
}

@Composable
fun loadModelFile(): MappedByteBuffer? {
    val fileDescriptor: AssetFileDescriptor = LocalContext.current.getAssets().openFd("${GlobalModelValues.ModelName}.tflite")
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declareLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength)
}