package tech.zettervall.notes.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.PersistableBundle;

import androidx.core.app.NotificationCompat;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.Constants;
import tech.zettervall.notes.MainActivity;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * Notification for Note Reminders.
 */
public class NotificationJobService extends JobService {

    private static final String TAG = NotificationJobService.class.getSimpleName();
    private static final String PRIMARY_CHANNEL_ID = "reminders_notification_channel";
    private NotificationManager mNotificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle persistableBundle = params.getExtras();
        int noteID = persistableBundle.getInt(Constants.NOTE_ID);

        // Reset Note Notification
        NoteRepository noteRepository = NoteRepository.getInstance(getApplication());
        Note note = noteRepository.getNote(noteID);
        if (note != null) {
            note.setNotificationEpoch(-1);
            noteRepository.updateNote(note);

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
                    .setContentTitle(note.getTitle())
                    .setContentText(note.getText())
                    .setContentIntent(contentPendingIntent)
                    .setSmallIcon(R.drawable.ic_edit)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);

            // Send Notification
            mNotificationManager.notify(0, builder.build());
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true; // Return true so that if the job fails, it's rescheduled
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
                    (PRIMARY_CHANNEL_ID, getString(R.string.notification_channel_reminders_title), NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
