package com.example.performmonitorutil

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylibrary.ResourceMonitorService


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var adapter: MetricAdapter? = null
    private var metrics: MutableList<Metric> = mutableListOf()

    private val metricsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val memory = intent.getStringExtra("memory")
            val cpu = intent.getStringExtra("cpu")
            val threads = intent.getStringExtra("threads")
            if (memory != null) {
                if (cpu != null) {
                    if (threads != null) {
                        updateMetrics(memory, cpu, threads)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        metrics.clear()
        // Initialize RecyclerView
        // Initialize RecyclerView
        metrics = ArrayList()
        adapter = MetricAdapter(metrics)

        val recyclerView = findViewById<RecyclerView>(R.id.metricsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =adapter;
        Log.d(TAG,"STARTED")
        // Register the BroadcastReceiver
        val filter = IntentFilter("com.example.mylibrary.METRICS_UPDATE")
        registerReceiver(metricsReceiver, filter)

        // Start the service

        // Start the service
        try {
            val serviceIntent = Intent(this, ResourceMonitorService::class.java)
            startService(serviceIntent)
        }catch (e:Exception){
            Log.d(TAG,"Exception")

        }

    }

    private fun updateMetrics(memory: String, cpu: String, threads: String) {

        Log.d(TAG,metrics.size.toString())
        Log.d(TAG,"Memory Usage : $memory")
        metrics.add(Metric("Memory Usage : $memory",
            "CPU Time : $cpu" ,"Active Threads : $threads"))
        Log.d(TAG,metrics.get(metrics.size-1).toString())
        runOnUiThread {
            adapter!!.notifyDataSetChanged()
        }

    }


    data class Metric(
        val memoryUsage:String,
        val cpuTime:String,
        val activeThreads:String
    )

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(metricsReceiver);
    }
}