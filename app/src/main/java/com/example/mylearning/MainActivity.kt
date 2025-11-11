package com.example.mylearning

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //test file model
        val testFile = File("/data/local/tmp/test.txt")
        val model = FileModel(testFile)
        Log.d("TEST", "Name: ${model.name}")
        Log.d("TEST", "Size: ${model.getFormattedSize()}")
        Log.d("TEST", "Date: ${model.getFormattedDate()}")
    }
}