package com.baolan2005.jcampconverter

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = assets.open("testdata/test_file_1.dx")

        val reader = JcampReader(input)
        val jcamp = reader.jcamp
        if (jcamp != null) {
            for (spec in jcamp.spectra) {
                Log.d("baolanlequang", spec.xValues.size.toString())
            }
        }

    }
}