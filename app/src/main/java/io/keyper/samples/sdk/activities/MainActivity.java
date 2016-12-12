package io.keyper.samples.sdk.activities;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.keyper.android.sdk.KeyperSDK;
import io.keyper.android.sdk.KeyperTicketsActivity_;
import io.keyper.samples.sdk.R;
import io.keyper.samples.sdk.SampleApp;
import io.keyper.samples.sdk.services.GCMRegistrationIntentService;

public class MainActivity extends AppCompatActivity {

  private static final String ROUTE_IDENTIFIER = "keyper";
  private static final String HOST_APP_TOKEN = "6b5cf70e-aa72-4c7a-94f4-36dce704a19b";

  // in some cases the keyper sdk needs to prompt the user for some input.
  // when this happens, the keyper sdk will broadcast an event. Once such an event is captured, the
  // app should finish what it is doing and start the ticket area.
  private BroadcastReceiver mDeepLinkBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      authenticateAndStartKeyper(null);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // set the content view
    setContentView(R.layout.activity_main);

    // setup the toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // setup the starting point
    View button = findViewById(R.id.btn_start_sdk);
    if (button != null) {
      // adds a click listener that authenticates and starts the mobile ticketing area.
      button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          authenticateAndStartKeyper(null);
        }
      });
    }

    // registers this app for push notifications (you have to add a GCM id in build.gradle)
    registerForPushNotifications();

    // checks if the activity is started from a notification. This is just an example. Read more in the README and in
    // SampleGCMListenerService
    Intent intent = getIntent();
    boolean shouldAuthenticateAndStartKeyper = intent.getBooleanExtra("AuthenticateAndShowKeyperTickets", false);
    if (shouldAuthenticateAndStartKeyper) {
      authenticateAndStartKeyper(null);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    // we register a broadcast receiver if the keyper sdk has to summon the keyper root view.
    LocalBroadcastManager.getInstance(this).registerReceiver(mDeepLinkBroadcastReceiver, new IntentFilter(KeyperSDK.Events.KPR_BCR_ACTION_SHOW_KEYPER_SCREEN));

    // we obtain a branch instance and wait for it to finish.
    Branch branch = Branch.getInstance();
    branch.initSession(new Branch.BranchReferralInitListener(){
      @Override
      public void onInitFinished(JSONObject referringParams, BranchError error) {
        // if there is no error and the branch.io deep link is meant for keyper, we pass it to the sdk
        if (error == null && keyper().isKeyperDeepLink(referringParams)) {
          authenticateAndStartKeyper(referringParams);
        }
      }
    }, this.getIntent().getData(), this);

  }


  @Override
  protected void onStop() {

    // unregister the broadcast receiver for deep link events.
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeepLinkBroadcastReceiver);

    super.onPause();
  }

  // for branch.io
  @Override
  public void onNewIntent(Intent intent) {
    this.setIntent(intent);
  }

  /*
      Displays an option to logout if the sdk is authenticated.
     */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (keyper().isAuthenticated()) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
    } else {
      menu.clear();
    }
    return true;
  }

  /*
    Handles a logout
   */
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
    The methods checks if the keyper sdk has a user logged in and authenticates if necessary.

    Note that the default route identifier "keyper" works only in the sandbox environment. You can setup your own
    in the B2B web app.

    Note that the provided auth_token might be invalid when you try this code. To generate a new one, use the
    keyper_auth_token.sh provided with this sample.

    Note that the branch.io branchParamters are passed to the sdk. If they contain keyper deep links, the sdk
    will handle them correctly.
   */
  private void authenticateAndStartKeyper(JSONObject branchParameters) {
    keyper().handleDeepLinkParams(branchParameters);

    if (keyper().isAuthenticated()) {
      startKeyper();
    } else {
      keyper().login(ROUTE_IDENTIFIER, HOST_APP_TOKEN, new KeyperSDK.SessionCallback() {
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
    keyper().logout(new KeyperSDK.SessionCallback() {
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

  private KeyperSDK keyper() {
    return ((SampleApp) getApplication()).getKeyperSDK();
  }
}
