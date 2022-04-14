package com.baolan2005.jcampconverter

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
//        val input = assets.open("testdata/test_file_5.dx")
        val input = URL("https://raw.githubusercontent.com/baolanlequang/jcamp-converter-ios/master/JcampConverter/TestJcamp/testdata/nmr/File012.dx").openStream()
        val reader = JcampReader(input)
        val jcamp = reader.jcamp
        if (jcamp != null) {
            if (jcamp.hasChild()) {
                val firstChild = jcamp.children!![0]
                for (spec in firstChild.spectra) {
                    Log.d("baolanlequang", spec.xValues.size.toString())
                }
            }
            for (spec in jcamp.spectra) {
                Log.d("baolanlequang", spec.xValues.size.toString())
            }
        }
        else {
            Log.d("baolanlequang", "null")
        }

    }
}