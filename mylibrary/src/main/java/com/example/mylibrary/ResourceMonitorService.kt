package com.example.mylibrary

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Debug
import android.os.Handler
import android.os.IBinder
import android.os.Process.getElapsedCpuTime
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ResourceMonitorService : Service() {
    private val TAG = "ResourceMonitorService"
    private val scheduler = Executors.newScheduledThreadPool(1)
    private val handler: Handler = Handler()
    override fun onBind(intent: Intent?): IBinder? {
        return null // This is a started service, not a bound service
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"STARTED")
        scheduler.scheduleAtFixedRate({

            // Collect memory info
            val memoryInfo: Debug.MemoryInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(memoryInfo)
            val totalPss: Int = memoryInfo.getTotalPss() // in KB

            // Collect CPU info
            val cpuTimeMillis: Long =
                getElapsedCpuTime() // in milliseconds

            // Collect active thread count
            val activeThreadCount = Thread.activeCount()

            // Send collected data to the main thread
            handler.post { logResourceUsage(totalPss, cpuTimeMillis, activeThreadCount) }
        }, 0, 10, TimeUnit.SECONDS) // Adjust the period as needed
    }


    private fun sendBroadcastWithMetrics(
        totalPss: Int,
        cpuTimeMillis: Long,
        activeThreadCount: Int
    ) {
        val intent = Intent("com.example.mylibrary.METRICS_UPDATE")
        intent.putExtra("memory", "$totalPss KB")
        intent.putExtra("cpu", "$cpuTimeMillis ms")
        intent.putExtra("threads", activeThreadCount.toString())

        sendBroadcast(intent)
    }

    private fun logResourceUsage(totalPss: Int, cpuTimeMillis: Long, activeThreadCount: Int) {
        Log.d(TAG,"totalPss=$totalPss")
        Log.d(TAG,"cpuTimeMillis=$cpuTimeMillis")
        Log.d(TAG,"activeThreadCount=$activeThreadCount")

        sendBroadcastWithMetrics(totalPss, cpuTimeMillis, activeThreadCount)
    }
    override fun onDestroy() {
        super.onDestroy()
        scheduler.shutdownNow()
    }
}