package io.keyper.samples.sdk.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by petar@creativepragmatics.com on 30/05/16.
 */
public final class FontUtils {

  public static final String LOBSTER_REGULAR = "lobster/Lobster-Regular.ttf";

  private static final Map<String, Typeface> FONTS = new HashMap<String, Typeface>();

  private FontUtils() {

  }

  public static Typeface getFont(Context context, String key) {
    Typeface typeface = FONTS.get(key);

    if (typeface == null) {
      synchronized (FONTS) {
        try {
          typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + key);
          FONTS.put(key, typeface);
        } catch (Exception e) {
          typeface = Typeface.DEFAULT;
        }
      }
    }

    return typeface;
  }
}