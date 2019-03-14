package io.keyper.samples.sdk.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import io.keyper.android.sdk.KeyperTicketsFragment_;
import io.keyper.samples.sdk.R;

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
