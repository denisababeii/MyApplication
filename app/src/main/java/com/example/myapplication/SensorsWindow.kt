package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class SensorsWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors_window)

        var sensorManager: SensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        var lv = findViewById(R.id.listView) as ListView

        val listItems = arrayOfNulls<String>(sensorList.size)
        for (i in 0 until sensorList.size) {
            val sensor = sensorList[i]
            listItems[i] = sensor.name
        }
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listItems)
        lv.adapter = adapter
    }
}