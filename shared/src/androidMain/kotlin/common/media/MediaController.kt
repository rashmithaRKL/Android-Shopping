package common.media

import android.content.Context
import android.media.MediaRecorder
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class MediaController(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    
    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> = _recordingState
    
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private var currentOutputFile: File? = null

    fun startVideoRecording(onError: (String) -> Unit) {
        try {
            createMediaRecorder().apply {
                setVideoSource(MediaRecorder.VideoSource.CAMERA)
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoSize(1280, 720)
                setVideoFrameRate(30)
                
                val outputFile = createOutputFile("VIDEO")
                setOutputFile(outputFile.absolutePath)
                currentOutputFile = outputFile

                prepare()
                start()
                
                mediaRecorder = this
                _recordingState.value = RecordingState.Recording
            }
        } catch (e: Exception) {
            _recordingState.value = RecordingState.Error(e.message ?: "Unknown error")
            onError(e.message ?: "Failed to start video recording")
        }
    }

    fun startAudioRecording(onError: (String) -> Unit) {
        try {
            createMediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                
                val outputFile = createOutputFile("AUDIO")
                setOutputFile(outputFile.absolutePath)
                currentOutputFile = outputFile

                prepare()
                start()
                
                mediaRecorder = this
                _recordingState.value = RecordingState.Recording
            }
        } catch (e: Exception) {
            _recordingState.value = RecordingState.Error(e.message ?: "Unknown error")
            onError(e.message ?: "Failed to start audio recording")
        }
    }

    fun stopRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _recordingState.value = RecordingState.Idle
            currentOutputFile
        } catch (e: Exception) {
            _recordingState.value = RecordingState.Error(e.message ?: "Unknown error")
            null
        }
    }

    fun startPlayback(file: File, onError: (String) -> Unit) {
        try {
            MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(file))
                setOnPreparedListener {
                    start()
                    _playbackState.value = PlaybackState.Playing
                }
                setOnCompletionListener {
                    _playbackState.value = PlaybackState.Completed
                }
                setOnErrorListener { _, _, _ ->
                    _playbackState.value = PlaybackState.Error("Playback error")
                    true
                }
                prepareAsync()
                mediaPlayer = this
            }
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.Error(e.message ?: "Unknown error")
            onError(e.message ?: "Failed to start playback")
        }
    }

    fun pausePlayback() {
        try {
            mediaPlayer?.pause()
            _playbackState.value = PlaybackState.Paused
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.Error(e.message ?: "Unknown error")
        }
    }

    fun resumePlayback() {
        try {
            mediaPlayer?.start()
            _playbackState.value = PlaybackState.Playing
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.Error(e.message ?: "Unknown error")
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.apply {
                stop()
                release()
            }
            mediaPlayer = null
            _playbackState.value = PlaybackState.Idle
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.Error(e.message ?: "Unknown error")
        }
    }

    fun release() {
        stopRecording()
        stopPlayback()
    }

    private fun createMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    private fun createOutputFile(prefix: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${prefix}_${timeStamp}.mp4"
        val storageDir = context.getExternalFilesDir(null)
        return File(storageDir, fileName)
    }
}

sealed class RecordingState {
    object Idle : RecordingState()
    object Recording : RecordingState()
    data class Error(val message: String) : RecordingState()
}

sealed class PlaybackState {
    object Idle : PlaybackState()
    object Playing : PlaybackState()
    object Paused : PlaybackState()
    object Completed : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}
