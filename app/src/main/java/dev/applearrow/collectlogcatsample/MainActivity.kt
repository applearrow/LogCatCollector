package dev.applearrow.collectlogcatsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.applearrow.collectlog.collectLatestLogs
import dev.applearrow.collectlogcatsample.databinding.ActivityMainBinding
import timber.log.Timber
import timber.log.Timber.DebugTree


private lateinit var binding: ActivityMainBinding

/**
 * collectLatestLogs("--- doPartialRefreshData started ---")
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Timber.plant(DebugTree())
    }

    override fun onStart() {
        super.onStart()
        Timber.d("COLLECT_LOG_START")
        Timber.d("hello there")
        Timber.d("hello again")
        binding.textView.text = collectLatestLogs("COLLECT_LOG_START")
    }
}