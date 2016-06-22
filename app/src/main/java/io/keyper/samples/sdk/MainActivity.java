package io.keyper.samples.sdk;

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

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void authenticateAndStartKeyper() {
    KeyperSDK keyper = ((App) getApplication()).getKeyperSDK();

    if (keyper.isAuthenticated()) {
      startKeyper();
    } else {
      String routeIdentifier = "keyper"; // or your own from the B2B webapp
      String hostAppSessionToken = "[INSERT YOUR TOKEN HERE]"; // you can obtain a token by executing the create_test_user.sh script
      keyper.login(routeIdentifier, hostAppSessionToken, new KeyperSDK.SessionCallback() {
        @Override
        public void onSuccess() {
          startKeyper();
        }

        @Override
        public void onError() {
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
    //startActivity(new Intent(MainActivity.this, CustomXMLTicketsActivity.class));
  }
}
