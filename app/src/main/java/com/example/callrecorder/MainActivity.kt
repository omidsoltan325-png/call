package com.example.callrecorder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val requiredPermissions = mutableListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.MODIFY_AUDIO_SETTINGS
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    private val permissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            Toast.makeText(this, "همه مجوزها داده شد. برنامه آماده ضبط مکالمه است.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "بدون این مجوزها برنامه نمی‌تواند مکالمه را ضبط کند.", Toast.LENGTH_LONG).show()
        }
        refreshList()
    }

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listRecordings)
        findViewById<TextView>(R.id.txtInfo).text =
            "ضبط‌ها در پوشه داخلی برنامه ذخیره می‌شوند:\n${recordingsDir().absolutePath}\n\n" +
            "توجه: این روش با روشن‌کردن خودکار بلندگو حین تماس کار می‌کند تا صدای هر دو طرف وارد میکروفون شود."

        requestNeededPermissions()
        refreshList()
    }

    private fun requestNeededPermissions() {
        val notGranted = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            permissionLauncher.launch(notGranted.toTypedArray())
        }
    }

    private fun recordingsDir() = getExternalFilesDir("CallRecordings") ?: filesDir

    private fun refreshList() {
        val dir = recordingsDir()
        val files = dir.listFiles()?.sortedByDescending { it.lastModified() }?.map { it.name } ?: emptyList()
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, files)
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }
}
