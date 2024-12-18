package com.example.mylibrary

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Debug
import android.os.Handler
import android.os.IBinder
import android.os.Process.getElapsedCpuTime
import android.util.Log
import java.io.File
import java.lang.System.nanoTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ResourceMonitorService : Service() {
    private val TAG = "ResourceMonitorService"
    private val scheduler = Executors.newScheduledThreadPool(1)
    private var lastCpuTime: Long = 0
    private var lastNanoTime: Long = 0
    private val handler: Handler = Handler()
    override fun onBind(intent: Intent?): IBinder? {
        return null // This is a started service, not a bound service
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"STARTED")
        // Initializing the first CPU time reading
        lastCpuTime = getElapsedCpuTime()
        lastNanoTime = nanoTime()
        scheduler.scheduleAtFixedRate({

            // Collect memory info
            val memoryInfo: Debug.MemoryInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(memoryInfo)
            val totalPss: Int = memoryInfo.getTotalPss() // in KB

            // CPU Load Monitoring
            val cpuLoad = startCpuLoadMonitoring()

            // Collect active thread count
            val activeThreadCount = Thread.activeCount()

            // Send collected data to the main thread
            handler.post { logResourceUsage(totalPss, cpuLoad, activeThreadCount) }
        }, 0, 10, TimeUnit.SECONDS) // Adjust the period as needed
    }

    private fun startCpuLoadMonitoring():Double {
        // Get the current CPU time and elapsed time
        val currentCpuTime = getElapsedCpuTime()
        val currentNanoTime = nanoTime()

        // Calculate CPU Load (using the difference in CPU time)
        val cpuTimeDiff = currentCpuTime - lastCpuTime
        val timeDiff = (currentNanoTime - lastNanoTime)  // Convert to seconds

        // Calculate CPU load as a percentage
        val cpuLoad = if (timeDiff > 0) {
            (cpuTimeDiff.toDouble() / timeDiff) * 100
        } else {
            0.0
        }

        // Log or display the CPU load
        println("CPU Load: $cpuLoad")
        println("timeDiff: $timeDiff")

        // Update the last CPU time and nano time for the next measurement
        lastCpuTime = currentCpuTime
        lastNanoTime = currentNanoTime
        return cpuLoad;
    }


    private fun sendBroadcastWithMetrics(
        totalPss: Int,
        cpuLoadPercentage: Double,
        activeThreadCount: Int
    ) {
        val intent = Intent("com.example.mylibrary.METRICS_UPDATE")
        intent.putExtra("memory", "$totalPss KB")
        intent.putExtra("cpu", "$cpuLoadPercentage %")
        intent.putExtra("threads", activeThreadCount.toString())

        sendBroadcast(intent)
    }

    private fun logResourceUsage(totalPss: Int, cpuLoadPercentage: Double, activeThreadCount: Int) {
        Log.d(TAG,"totalPss=$totalPss")
        Log.d(TAG,"cpuLoadPercentage=$cpuLoadPercentage")
        Log.d(TAG,"activeThreadCount=$activeThreadCount")

        sendBroadcastWithMetrics(totalPss, cpuLoadPercentage, activeThreadCount)
    }
    override fun onDestroy() {
        super.onDestroy()
        scheduler.shutdownNow()
    }
}