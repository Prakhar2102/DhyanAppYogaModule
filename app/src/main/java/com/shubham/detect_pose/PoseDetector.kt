package com.shubham.detect_pose

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult


class PoseDetector(private val context: Context) {
    private val poseResult = mutableStateOf<ResultBundle?>(null)
    private val poseLandmarkerHelperListener=MyLandmarkerListener()

    fun initializePoseLandmarker(): PoseLandmarker {
        val modelName = "pose_landmarker_lite.task"
        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelName)

        val optionsBuilder =
            PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setMinPoseDetectionConfidence(0.5F)
                .setMinTrackingConfidence(0.5F)
                .setMinPosePresenceConfidence(0.5F)
                .setNumPoses(1)
                .setResultListener(this::returnLivestreamResult)
                .setErrorListener(this::returnLivestreamError)
                .setRunningMode(RunningMode.LIVE_STREAM)
        val options = optionsBuilder.build()
        return PoseLandmarker.createFromOptions(context, options)
    }
    fun getPoseResult() = poseResult
    private fun returnLivestreamResult(
        result: PoseLandmarkerResult,
        input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        poseResult.value = ResultBundle(
            listOf(result),
            inferenceTime,
            input.height,
            input.width
        )

        poseLandmarkerHelperListener.onResults(
            ResultBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

        private fun returnLivestreamError(error: RuntimeException) {
            poseLandmarkerHelperListener.onError(
                error.message ?: "An unknown error has occurred"
            )
        }
        companion object {
            const val OTHER_ERROR = 0

        }
        data class ResultBundle(
            val results: List<PoseLandmarkerResult>,
            val inferenceTime: Long,
            val inputImageHeight: Int,
            val inputImageWidth: Int,
        )
        interface LandmarkerListener {
            fun onError(error: String, errorCode: Int = OTHER_ERROR)
            fun onResults(resultBundle: ResultBundle)
        }
    }


class MyLandmarkerListener : PoseDetector.LandmarkerListener {
    override fun onError(error: String, errorCode: Int) {
        // Handle error
        Log.e("MyLandmarkerListener", "Error occurred: $error, Code: $errorCode")
    }

    override fun onResults(resultBundle: PoseDetector.ResultBundle) {

        Log.d("MyLandmarkerListener", "Received results: $resultBundle")
    }

}