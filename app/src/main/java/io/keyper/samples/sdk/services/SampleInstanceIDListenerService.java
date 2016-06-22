package io.keyper.samples.sdk.services;

import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;

/**
 * Created by petar@creativepragmatics.com on 22/06/16.
 */
public class SampleInstanceIDListenerService extends InstanceIDListenerService {

  @Override
  public void onTokenRefresh() {
    super.onTokenRefresh();
    startService(new Intent(SampleInstanceIDListenerService.this, GCMRegistrationIntentService.class));
  }
}
