package com.shubham.detect_pose


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Navigation(context)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources
        finish()
    }
}


@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()
    val poseDetector = PoseDetector(context = context)
    val poseLandmarker = poseDetector.initializePoseLandmarker()
    lateinit var executor: Executor
    val poseResult = poseDetector.getPoseResult()
    executor = Executors.newSingleThreadExecutor()
    NavHost(navController = navController, startDestination = "PoseScreen") {
        composable("PoseScreen") {
            // This is the camera preview screen

            YogaScreen(poseLandmarker, executor,poseResult)

        }
    }
}