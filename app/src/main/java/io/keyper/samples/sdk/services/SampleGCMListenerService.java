package io.keyper.samples.sdk.services;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import io.keyper.android.sdk.KeyperNotificationRecommendedHostAppAction;
import io.keyper.android.sdk.KeyperSDK;
import io.keyper.android.sdk.KeyperTicketsActivity_;
import io.keyper.samples.sdk.SampleApp;
import io.keyper.samples.sdk.activities.MainActivity;

/**
 * Created by petar@creativepragmatics.com on 22/06/16.
 */
public class SampleGCMListenerService extends GcmListenerService {

  @Override
  public void onMessageReceived(String s, Bundle bundle) {
    super.onMessageReceived(s, bundle);

    KeyperSDK sdk = ((SampleApp) getApplication()).getKeyperSDK();

    if (sdk.isKeyperNotification(bundle)) {
      KeyperNotificationRecommendedHostAppAction recommendedAction = sdk.getRecommendedAction(bundle);
      PendingIntent pendingIntent = null;
      switch (recommendedAction) {
        case NO_ACTION:
          pendingIntent = skipNotification();
          break;
        case SHOW_KEYPERTICKETS:
          pendingIntent = showKeyperTickets();
          break;
        case AUTHENTICATE_AND_SHOW_KEYPERTICKETS:
          pendingIntent = authenticateAndShowKeyperTickets();
          break;
      }

      // if you just want to display the notification in the status bar
      sdk.displayNotificationInStatusBar(bundle, pendingIntent);


      // if you want to display an in app notification then you obtain some basic textual info here
      // sdk.getKeyperNotification(bundle)

    } else {
      Log.d("GCM", "notification received: " + bundle);
      // TODO handle your own notification...
    }
  }

  /*
    There is no need to start any activity.
   */
  private PendingIntent skipNotification() {
    Log.d("GCM", "Notification received. No need for app reaction");
    return null;
  }

  /*
    Starts the Keyper Tickets Activity. If you want you can start your own activity with the keyper tickets fragment
    or any other activity for that matter.
   */
  private PendingIntent showKeyperTickets() {
    Log.d("GCM", "Notification received. Show keyper Tickets.");
    Intent intent = KeyperTicketsActivity_
        .intent(this)
        .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        .get();
    return PendingIntent.getActivity(this, 0, intent, 0);
  }

  /*
    Starts the main activity and passes a flag to authenticate and start the keyper tickets.
    Note, that this is a sample implementation and you can adjust this as needed for your own app.
   */
  private PendingIntent authenticateAndShowKeyperTickets() {
    Log.d("GCM", "Notification received. Authenticate and show keyper Tickets.");
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra("AuthenticateAndShowKeyperTickets", true);
    return PendingIntent.getActivity(this, 0, intent, 0);
  }

}
