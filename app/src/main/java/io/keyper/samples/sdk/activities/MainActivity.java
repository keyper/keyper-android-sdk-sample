package io.keyper.samples.sdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.keyper.android.sdk.KeyperSDK;
import io.keyper.android.sdk.KeyperTicketsActivity_;
import io.keyper.samples.sdk.R;
import io.keyper.samples.sdk.SampleApp;
import io.keyper.samples.sdk.services.GCMRegistrationIntentService;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    View button = findViewById(R.id.btn_start_sdk);
    if (button != null) {
      button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          authenticateAndStartKeyper();
        }
      });
    }

    registerForPushNotifications();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    KeyperSDK keyper = ((SampleApp) getApplication()).getKeyperSDK();
    if (keyper.isAuthenticated()) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
    } else {
      menu.clear();
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_logout) {
      logout();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /*
    Starts a service that registers the device for push notifications with the keyper service.

    Do not forget to add your own GCM Sender ID in the build.gradle file
   */
  private void registerForPushNotifications() {
    startService(new Intent(MainActivity.this, GCMRegistrationIntentService.class));
  }

  /*
    Starts the keyper ticketing area.
    The methods first checks if the keyper sdk has a user logged in and authenticates if necessary.

    Note that the default route identifier "keyper" works only in the sandbox environment. You can setup your own
    in the B2B web app.

    Note that the provided auth_token might be invalid when you try this code. To generate a new one, use the
    keyper_auth_token.sh provided with this sample.
   */
  private void authenticateAndStartKeyper() {
    KeyperSDK keyper = ((SampleApp) getApplication()).getKeyperSDK();

    if (keyper.isAuthenticated()) {
      startKeyper();
    } else {
      String routeIdentifier = "keyper";
      String hostAppSessionToken = "3389a825-c5f0-4370-ac2d-888a4121f40e";
      keyper.login(routeIdentifier, hostAppSessionToken, new KeyperSDK.SessionCallback() {
        @Override
        public void onSuccess() {
          invalidateOptionsMenu();
          startKeyper();
        }

        @Override
        public void onError() {
          invalidateOptionsMenu();
          Toast.makeText(MainActivity.this, R.string.err_keyper_login_failed, Toast.LENGTH_LONG).show();
        }
      });
    }
  }

  private void startKeyper() {
    KeyperTicketsActivity_.intent(this).start();

    // or use your own activity with the KeyperTicketsFragment. For an example, comment the line above and
    // uncomment one of the lines below:

    //startActivity(new Intent(MainActivity.this, CustomXMLTicketsActivity.class));
    //startActivity(new Intent(MainActivity.this, CustomTicketsActivity.class));
  }

  /*
    Logs out from the keyper service and invalidates the options menu.
   */
  private void logout() {
    KeyperSDK keyper = ((SampleApp) getApplication()).getKeyperSDK();
    keyper.logout(new KeyperSDK.SessionCallback() {
      @Override
      public void onSuccess() {
        invalidateOptionsMenu();
      }

      @Override
      public void onError() {
        invalidateOptionsMenu();
      }
    });
  }
}
