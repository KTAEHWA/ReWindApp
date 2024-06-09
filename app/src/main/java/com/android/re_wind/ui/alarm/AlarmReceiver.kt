package com.android.re_wind.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message")
        Toast.makeText(context, "알람: $message", Toast.LENGTH_SHORT).show()

    }
}
