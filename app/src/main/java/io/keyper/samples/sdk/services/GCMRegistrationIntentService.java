package io.keyper.samples.sdk.services;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import io.keyper.android.sdk.KeyperSDK;
import io.keyper.samples.sdk.BuildConfig;
import io.keyper.samples.sdk.SampleApp;

/**
 * Created by petar@creativepragmatics.com on 22/06/16.
 */
public class GCMRegistrationIntentService extends IntentService {

  public GCMRegistrationIntentService() {
    super("GCMRegistrationIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {

      InstanceID instanceID = InstanceID.getInstance(this);
      String token = instanceID.getToken(BuildConfig.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      sendRegistrationToServer(token);

    } catch (Exception e) {
      Log.d("GCM", "Failed to complete token refresh. If you want to test the push notifications, add your own GCM id in build.gradle", e);
    }
  }

  private void sendRegistrationToServer(String token) {
    // TODO send the token to your server for further processing of your own notifications...

    Log.d("GCM", token);

    KeyperSDK sdk = ((SampleApp) getApplication()).getKeyperSDK();
    if (sdk.isInitialized()) {
      sdk.subscribeForPushNotifications(token);
    }
  }
}
