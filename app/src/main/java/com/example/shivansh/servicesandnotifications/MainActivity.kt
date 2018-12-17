package com.example.shivansh.servicesandnotifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.shivansh.servicesandnotifications.utilities.PreferenceUtilities
import com.example.shivansh.servicesandnotifications.sync.ReminderTasks
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import com.example.shivansh.servicesandnotifications.sync.WaterReminderIntentService
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Button
import com.example.shivansh.servicesandnotifications.utilities.NotificationUtlis
import com.example.shivansh.servicesandnotifications.utilities.PreferenceUtilities.KEY_WATER_COUNT


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var mWaterCountDisplay: TextView? = null
    private var mChargingCountDisplay: TextView? = null
    private var mChargingImageView: ImageView? = null
    private var mNotificationcheck: Button?=null

    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Get the views  */
        mWaterCountDisplay = findViewById(R.id.tv_water_count) as TextView
        mChargingCountDisplay = findViewById(R.id.tv_charging_reminder_count) as TextView
        mChargingImageView = findViewById(R.id.iv_power_increment) as ImageView
        mNotificationcheck = findViewById(R.id.notibutton) as Button

        mNotificationcheck?.setOnClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            editor.putInt(KEY_WATER_COUNT, 0)
            editor.apply()
        }
        /** Set the original values in the UI  */
        updateWaterCount()
        updateChargingReminderCount()

        NotificationUtlis.scheduleNotifications(this);
        /** Setup the shared preference listener  */
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * Updates the TextView to display the new water count from SharedPreferences
     */
    private fun updateWaterCount() {
        Log.e("log","Water Count Updated")
        val waterCount = PreferenceUtilities.getWaterCount(this)
        mWaterCountDisplay!!.setText(waterCount.toString() + "")
    }

    /**
     * Updates the TextView to display the new charging reminder count from SharedPreferences
     */
    private fun updateChargingReminderCount() {
        val chargingReminders = PreferenceUtilities.getChargingReminderCount(this)
        val formattedChargingReminders = resources.getQuantityString(
                R.plurals.charge_notification_count, chargingReminders, chargingReminders)
        mChargingCountDisplay!!.text = formattedChargingReminders

    }

    /**
     * Adds one to the water count and shows a toast
     */
    fun incrementWater(view: View) {
        if (mToast != null) mToast!!.cancel()
        mToast = Toast.makeText(this, "Increment Water", Toast.LENGTH_SHORT)
        mToast!!.show()

      //  Log.e("log","Khel aarambh");
        val incrementWaterCountIntent = Intent(this, WaterReminderIntentService::class.java)
        incrementWaterCountIntent.action = ReminderTasks.ACTION_INCREMENT_WATER_COUNT
        startService(incrementWaterCountIntent)

    }

    override fun onDestroy() {
        super.onDestroy()
        /** Cleanup the shared preference listener  */
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * This is a listener that will update the UI when the water count or charging reminder counts
     * change
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (PreferenceUtilities.KEY_WATER_COUNT.equals(key)) {
            updateWaterCount()
        } else if (PreferenceUtilities.KEY_CHARGING_REMINDER_COUNT.equals(key)) {
            updateChargingReminderCount()
        }
    }
}