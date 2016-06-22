package io.keyper.samples.sdk;

import android.app.Application;

import io.keyper.android.sdk.KeyperSDK;

/**
 * Created by petar@creativepragmatics.com on 22/06/16.
 */
public class SampleApp extends Application {

  private KeyperSDK mKeyperSDK;

  @Override
  public void onCreate() {
    super.onCreate();

    initKeyperSDK();
  }

  public KeyperSDK getKeyperSDK() {
    if (mKeyperSDK == null) {
      initKeyperSDK();
    }

    return mKeyperSDK;
  }

  private void initKeyperSDK() {
    mKeyperSDK = KeyperSDK
        .with(this)
        .baseURL("https://sandbox.api.keyper.io/api") // this should be set only when BuildConfig.DEBUG == true
        .actionColorResource(R.color.colorAccent)
        .toolbarBackgroundColorResource(R.color.colorPrimary)
        .toolbarTextColorResource(R.color.white)
        .toolbarTitleSizeResource(R.dimen.toolbar_text_size)
        .toolbarTitleResource(R.string.lbl_toolbar_title)
        .toolbarTypeface(FontUtils.getFont(this, FontUtils.LOBSTER_REGULAR))
        .get();
  }
}
