package tech.zettervall.notes.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.Constants;
import tech.zettervall.notes.MainActivity;

public class NotificationJobService extends JobService {

    private static final String TAG = NotificationJobService.class.getSimpleName();
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String PRIMARY_CHANNEL_NAME = "Primary Notification Channel";
    private NotificationManager mNotificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {

        PersistableBundle persistableBundle = params.getExtras();
        int noteID;
        String title, text;

        noteID = persistableBundle.getInt(Constants.NOTE_ID);
        title = persistableBundle.getString(Constants.NOTE_TITLE);
        text = persistableBundle.getString(Constants.NOTE_TEXT);

        // Create Notification channel
        createNotificationChannel();

        // Intent for when user clicks the Notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.NOTE_ID, noteID);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, PRIMARY_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_note)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        // Send Notification
        mNotificationManager.notify(0, builder.build());

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private void createNotificationChannel() {

        // Define notification manager object.
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            // Todo: set these depending on user preferences
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
