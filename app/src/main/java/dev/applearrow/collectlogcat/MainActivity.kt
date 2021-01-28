package dev.applearrow.collectlogcat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.applearrow.collectlogcat.databinding.ActivityMainBinding
import dev.applearrow.logcat.LogCatCollector
import timber.log.Timber

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        Timber.d(LOG_START)
        Timber.d("hello there")
        Timber.i("hi again")
        Timber.d("good bye")
        binding.textView.text =
            LogCatCollector("MainActivity").collect(LOG_START, true, listOf("good"))
    }

    companion object {
        const val LOG_START = "COLLECT_LOG_START"
    }
}