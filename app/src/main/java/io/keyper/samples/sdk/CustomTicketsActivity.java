package io.keyper.samples.sdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.keyper.android.sdk.KeyperTicketsFragment_;

/**
 * Created by petar@creativepragmatics.com on 22/06/16.
 */
public class CustomTicketsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom_tickets);

    getSupportFragmentManager()
        .beginTransaction().add(R.id.container, KeyperTicketsFragment_.builder().build())
        .commit();
  }
}
