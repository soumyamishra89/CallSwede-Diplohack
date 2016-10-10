package diplohack.callswede.view.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Random;

import diplohack.callswede.PlaceCallActivity;
import diplohack.callswede.R;
import diplohack.callswede.SnicksnackService;

public class GcmIntentService extends IntentService {
    public static final String NOTIFICATION_ID = "GcmIntentService";
    private NotificationManager mNotificationManager;
    private static final Random r = new Random();
    private final static String TAG = "GcmIntentService";

    public GcmIntentService(){
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Log.d(TAG, "Notification Data Json :" + extras.getString("message"));

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty() && messageType != null) {
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    //sendNotification("Send error: " + extras);
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    //sendNotification("Deleted messages on server: "+ extras);          // If it's a regular GCM message, do some work.
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    // This loop represents the service doing some work.
                    Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                    sendNotification(extras);
                    break;
            }
        }        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }    // Put the message into a notification and post it.

    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle msg){
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int notification_id = r.nextInt();

        Intent intent = new Intent(this, PlaceCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.v(TAG, msg.getString("recipientId"));
        intent.putExtra(SnicksnackService.RECIPIENT_ID, msg.getString("recipientId"));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
               .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.InboxStyle().setBigContentTitle(msg.getString("title")).setSummaryText(msg.getString("message")))
                .setContentTitle(msg.getString("title"))
                .setContentText(msg.getString("message"))
                .setSubText(msg.getString("subtitle"))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setColor(0x990066)
                .setAutoCancel(true)
                .setLights(0x036303, 500, 500);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }
}
