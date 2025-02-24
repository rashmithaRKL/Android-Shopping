package common.background

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class BackgroundTaskManager(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)
    
    private val _taskStatus = MutableStateFlow<Map<String, WorkInfo.State>>(emptyMap())
    val taskStatus: StateFlow<Map<String, WorkInfo.State>> = _taskStatus

    fun schedulePeriodicTask(
        taskName: String,
        repeatInterval: Duration = 15.minutes,
        constraints: Constraints = defaultConstraints(),
        inputData: Data = Data.EMPTY
    ) {
        val workRequest = PeriodicWorkRequestBuilder<BaseWorker>(
            repeatInterval.inWholeMinutes,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniquePeriodicWork(
            taskName,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        observeWorkStatus(workRequest.id)
    }

    fun scheduleOneTimeTask(
        taskName: String,
        constraints: Constraints = defaultConstraints(),
        inputData: Data = Data.EMPTY,
        delay: Duration? = null
    ) {
        val builder = OneTimeWorkRequestBuilder<BaseWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)

        if (delay != null) {
            builder.setInitialDelay(delay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        }

        val workRequest = builder.build()

        workManager.enqueueUniqueWork(
            taskName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        observeWorkStatus(workRequest.id)
    }

    fun cancelTask(taskName: String) {
        workManager.cancelUniqueWork(taskName)
    }

    fun cancelAllTasks() {
        workManager.cancelAllWork()
    }

    private fun observeWorkStatus(workId: UUID) {
        workManager.getWorkInfoByIdLiveData(workId).observeForever { workInfo ->
            workInfo?.let {
                _taskStatus.value = _taskStatus.value + (workId.toString() to it.state)
            }
        }
    }

    private fun defaultConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()
}

class BaseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Execute the background task
            val taskType = inputData.getString(KEY_TASK_TYPE) ?: return Result.failure()
            
            when (taskType) {
                TASK_TYPE_SYNC -> performSync()
                TASK_TYPE_CLEANUP -> performCleanup()
                TASK_TYPE_NOTIFICATION -> scheduleNotification()
                else -> Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun performSync(): Result {
        // Implement sync logic
        return Result.success()
    }

    private suspend fun performCleanup(): Result {
        // Implement cleanup logic
        return Result.success()
    }

    private suspend fun scheduleNotification(): Result {
        // Implement notification scheduling logic
        return Result.success()
    }

    companion object {
        const val KEY_TASK_TYPE = "task_type"
        const val TASK_TYPE_SYNC = "sync"
        const val TASK_TYPE_CLEANUP = "cleanup"
        const val TASK_TYPE_NOTIFICATION = "notification"
    }
}

sealed class TaskResult {
    object Success : TaskResult()
    data class Error(val message: String) : TaskResult()
    object InProgress : TaskResult()
}

// Extension function to create work data
fun workDataOf(taskType: String, vararg pairs: Pair<String, Any?>): Data {
    return Data.Builder()
        .putString(BaseWorker.KEY_TASK_TYPE, taskType)
        .apply {
            pairs.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Array<*> -> putStringArray(key, value.map { it.toString() }.toTypedArray())
                }
            }
        }
        .build()
}
