package io.keyper.samples.sdk.services;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;

import io.keyper.android.sdk.KeyperNotificationRecommendedHostAppAction;
import io.keyper.android.sdk.KeyperSDK;
import io.keyper.samples.sdk.SampleApp;

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

      sdk.handleNotification(bundle, pendingIntent);

    } else {
      Log.d("GCM", "notification received: " + bundle);
      // TODO handle your own notification...
    }
  }

  private PendingIntent skipNotification() {
    Log.d("GCM", "Unknown notification, skipping");
    return null;
  }

  private PendingIntent showKeyperTickets() {
    return null;
  }

  private PendingIntent authenticateAndShowKeyperTickets() {
    return null;
  }

}
