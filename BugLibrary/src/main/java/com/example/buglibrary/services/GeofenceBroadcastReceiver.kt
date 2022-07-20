package com.example.buglibrary.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.example.buglibrary.SDKActivity
import com.example.buglibrary.R
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.helper.PreferenceHelper
import com.example.buglibrary.helper.PreferenceHelper.get
import com.example.buglibrary.helper.PreferenceHelper.set
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    val TAG = "GeofenceBroadcastRecei"
    private val todaysDate = SimpleDateFormat.getDateInstance().format(Date())
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: error")
            return
        }
        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            Log.d(TAG, "onReceive: inside geofence")
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                context,
                geofenceTransition,
                triggeringGeofences
            )

            // Send notification and log the transition details.
            val pref = PreferenceHelper.defaultPrefs(context!!)
            val geoNotif: String? = pref[AppConstant.GEO_NOTI]
            Log.d(TAG, "onReceive: date $geoNotif")
            if (todaysDate != geoNotif) {
                pref[AppConstant.GEO_NOTI] = todaysDate
                sendNotification(
                    context,
                    context.getString(R.string.geofence_notif_title)!!,
                    context.getString(R.string.geofence_notif_body)
                )
            }
            Log.i(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e(TAG, "invalid $geofenceTransition")
        }

    }

    private fun getGeofenceTransitionDetails(
        context: Context?,
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String {
        val geofenceTransitionString: String? = getTransitionString(geofenceTransition)

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList: ArrayList<String> = ArrayList()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString: String = TextUtils.join(", ", triggeringGeofencesIdsList)
        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    private fun sendNotification(context: Context?, title: String, messageBody: String) {
        val intent = Intent(context, SDKActivity::class.java)
//        intent.putExtra(AppConstant.NOTIFICATION_TYPE, "notification")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = context?.getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context!!, channelId!!)
            .setSmallIcon(R.drawable.ic_status_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private fun getTransitionString(transitionType: Int): String? {
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "geofence_transition_entered"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "geofence_transition_exited"
            else -> "unknown_geofence_transition"
        }
    }
}