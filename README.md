# Keyper Android SDK Developers Guide
**SDK Version: 0.9.+**
 
Contact: *dev@keyper.io*

Last Updated: *23.06.2016*

The keyper SDK offers developers a complete mobile ticket solution, that they can include and use within their apps.

## Getting Started
In order to get started, fire up Android Studio and add the following snippet to the top of your app module `build.gradle` file.

```
repositories {
    jcenter()
    maven { url "http://keyper.bintray.com/keyper" }
}
```

If you already have a `repositories` closure, then just include the maven part.

Then include the keyper SDK as a dependency:

```
dependencies {
	compile 'io.keyper.android:keyper-sdk:0.9.0'
}
```

Hit Sync, gradle will build the project and you should be ready to go.

#### Troubleshooting
If you get the following error while building, then you have to enable multi dex support.

```
Error: The number of method references in a .dex file cannot exceed 64K.
Learn how to resolve this issue at https://developer.android.com/tools/building/multidex.html
```

To do so, just add the following line to your `defaultConfig` closure:

```
multiDexEnabled true
```

Depending on your Android Studio settings, you might get an error, that there is not enough heap space. If that is the case, then you can increase it by adding the following to your `android` closure:

```
dexOptions {
	javaMaxHeapSize "2g" // "4g"
}
```

## General
The `minSdkVersion` of the keyper SDK ist 15 (Android 4.0.3 IceCreamSandwich).

The SDK utilizes the following permissions:

 - `android.permission.INTERNET` 
 In order to connect with the keyper backend service.
 - `android.permission.READ_CONTACTS`
 In order to read the phone contacts, so that a user can send tickets to their contacts. The permission is handled correctly on Android 6+. The user is prompted the first time they start the keyper ticketing area.
 - `android.permission.WRITE_EXTERNAL_STORAGE`
 In order to cache some data on the device. The data is stored privately and is accessible only to the app (unless the device is rooted).
 - `android.permission.ACCESS_NETWORK_STATE` for GoogleMaps
 - `com.google.android.providers.gsf.permission.READ_GSERVICES` for GoogleMaps
 - `io.keyper.android.permission.MAPS_RECEIVE` for GoogleMaps
 
 The SDK utilizes its own Google Maps key.
 
## Sample App
If you want to see a working app, please checkout this repository.

Once you checkout the repository, you will have to do two things in order to test all features of the SDK.

1) Add your own gcm_id in `app/build.gradle` if you want to test the push notifications as well. If you are not sure how to do this, please consult the [GCM guide](https://developers.google.com/cloud-messaging/gcm).

2) Create and login with your own keyper test user with the `keyper_auth_token.sh` script. It will give you an auth token (uuid), which you can then change in the `MainActivity#HOST_APP_TOKEN`. You will have to add a new token each time you logout from the app.

Once you have done these two steps, you can run the app on an emulator or a device.

If you'd like to integrate the SDK in your own app, please consult the next chapter.

## Development
The SDK consists of a singleton instance that exposes some helper methods and a few classes (Activities, Fragments, etc), that help you integrate a complete mobile ticket solution to your app.

### Initialize
To initialize the SDK, you can use the following snippet in your Application or any Activity.

```
KeyperSDK sdk = KeyperSDK.with(this).get();
```        

`this` is a Context object. Note, that it is recommended to use an activity or the Application object, if you are making use of GCM and the keyper notifications. Read more in the Notifications chapter.

Keep a reference to the SDK in order to interact with it. Alternatively, you can obtain a reference to the SDK with:

```
KeyperSDK sdk = KeyperSDK.getInstance();
```

Note, however, that if it is not initialized some of the helper methods will throw an `IllegalStateException`.

To avoid this, you can check if the SDK is initialized by calling: `sdk.isInitialized();`

### Login
Once you have the SDK configured, you can connect a user of your app with the keyper service. To do so, you have to configure your keyper b2b account in the keyper [b2b web app](https://app.keyper.io/b2b.html#) or the keyper [b2b sandbox webapp](https://sandbox.app.keyper.io/b2b.html#).

There you can configure a route identifier, which you will need in order to oauth with the keyper service.

In order to authenticate the user against the keyper service, use the following code snippet:

```
sdk.login(routeIdentifier, userTokenOfHostApp);
// or
sdk.login(routeIdentifier, userTokenOfHostApp, sessionCallback)
```

The route identifier is configured in the b2b web app. The host app token is the session token of your user. The session callback provides two callback methods, that help you identify if the operation was successful or not.

Once you have authenticated your user, you can present the user with the mobile ticket part of the SDK.

If you are not sure if the SDK is authenticated, you can use the following helper method.

```
sdk.isAuthenticated();
```

### Display The Mobile Ticket Area
In order to display the mobile ticket area, you have two choices.

1) You can start a new activity (provided by the SDK) that is just added to your activity stack. This is easier to do but the less flexible from the two options.

To start the mobile ticket activity, use the follwing snipped:

```
KeyperTicketsActivity_.intent(this).start(); //"this" is a context or a fragment.
```

Note, that the SDK has to be authenticated.

2) You can incorporate a fragment in your own activity. This option gives you more flexibility. For example if you want to swap this fragment via a navigation drawer.

You can add the KeyperTicketsFragment via xml:

```
<fragment
        android:id="@+id/fragment_keyper_tickets"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:name="io.keyper.android.sdk.KeyperTicketsFragment_" />
```

or programmatically, like this:

```
KeyperTicketsFragment fragment = KeyperTicketsFragment_.builder().build();
```

### Logout
When you want to clear the keyper data from the device (e.g. when you logout your user), you can use the following methods, to do so:

```
sdk.logout();
// or
sdk.logout(sessionCallback);
```

The logout method will clear the keyper user session and all related data from the device as well as unsubscribe the device from the keyper push notifications (if they were configured).

You can use a sessionCallback (as within the login operation), if you want to get feedback, if the operation succeeded or not.

### Notifications
If you want your users to obtain keyper notifications, e.g. when they get a new ticket offer, or a new friend request, then you have to take the following actions.

1) submit the device registration id (GCM Push Token) to the sdk. You can do so, via:

```
sdk.subscribeForPushNotifications(gcmToken); // gcmToken is a String
```

You should call this method everytime, when the gcm token gets refreshed. Follow the link to see how to obtain and handle [GCM push notifications on android](https://developers.google.com/cloud-messaging/android/client).

2) In order to allow the keyper SDK to handle the received notificaitons, you have to propagate them to the SDK, once you received them. The SDK offers you a few helper methods, to help you do that.

Once you obtain a notification (a Bundle object usually in `GcmListenerService.onMessageReceived(String s, Bundle bundle)`), you can inspect the notification like so:

```
if (sdk.isKeyperNotification(bundle)) { ... }
```

**Note**: Do not forget to check if the sdk is initialized and initialize it properly. If your app was killed and the GcmListenerService was just started in background, the SDK will not be initialized.

If this is a keyper notification, the SDK gives you a few options how to handle it.
The easiest is, to just pass the `Bundle` object to

```
sdk.handleNotification(bundle);
```

A call to this method will inspect the notification, checking if the app is in background or foreground and handle the notificaitons accordingly. If the app is in background or not started, then a notificaiton will be shown within the system status bar. If the mobile ticket area is open, then the app will handle the notification silently and update its relevant views.

**Important**: If you have initialized the SDK with a context other than an Activity or Application, then the SDK cannot infer if the app is in background or foreground. In such cases a notification will always be displayed in the system status bar.

There is one issue with this type of notification handling. Nothing will happen, once the user clicks on the notification, which is not a great UX. In order to start the app, the SDK provides you with some hints and another version of the `handleNotification` method that allows you to set a `PendingIntent`.

You can use: `sdk.getRecommendedAction(bundle);`, which will return a `KeyperNotificationRecommendedHostAppAction` enum.

The enum has a few recommended actions, which you can use as a hint to infer what kind of PendingIntent to set. In general, you can prepare a pending intent showing the KeyperTicketsActivity or your own wrapping activity of KeyperTicketsFragment, or you have to first login and then start the KeyperTicketsActivity or your own one.

### Customization

The SDK allows you to customize the activities, fragments and views a bit, in order to make them feel more native to your own app. The configurator of the SDK (that you saw in the first chapter), offers a few more options. The following displays all available options and describes them one by one.

```
KeyperSDK keyperSDK = KeyperSDK
        .with(this)
        .baseURL("https://sandbox.api.keyper.io/api")
        .actionColorResource(R.color.colorAccent)
        .toolbarBackgroundColorResource(R.color.colorPrimary)
        .toolbarTextColorResource(R.color.white)
        .toolbarTitleSizeResource(R.dimen.toolbar_size)
        .toolbarTitleResource(R.string.lbl_toolbar_title)
        .toolbarTypeface(FontUtils.getFont(this, FontUtils.LOBSTER_REGULAR))
        .sendTicketBadgeDrawableResource(R.drawable.icn_badge)
        .get();
```

| method | description | default value|
|--------|-------------|--------------|
| baseURL(String) | Allows you to change the base url of the keyper service. For example, if, you want to develop against the keyper sandbox environment. | https://api.keyper.io/api |
| actionColorResource | Sets a color resource, that is used throughout the sdk, for specific elements and views. It is recommended to take the accent color.  | The default value resolves to: #00cc99 |
| toolbarBackgroundColorResource | Sets a color resource, that is used in the toolbars throughout activities in the SDK. | The default value resolves to: #00cc99 |
| toolbarTextColorResource | Sets a color resource for the text color used in toolbars throughout the SDK | The default value resolves to: #ffffff |
| toolbarTitleSizeResource | Sets a dimen resource for the text size used in the KeyperTicketsActivity. | The default value is: 10sp |
| toolbarTitleResource | Sets the i18n string resource used in the KeyperTicketsActivity. | The default value resolves to: "Tickets" |
| toolbarTypeface | Sets a custom typeface in the KeyperTicketsActivity toolbar. | The default is: Typeface.create("sans-serif-medium", Typeface.NORMAL); |
| sendTicketBadgeDrawableResource | Sets a custom badge in some keyper views, to denote that the user already is a user of your app. | The default is null |

**Note**: FontUtils is a helper class, that reads a custom font (lobster) from an asset. It is included in the sample project.

**Important**: If you initialize your SDK more than once in a single session, the values will be overwritten with the once you use last. If you customize the SDK make sure, you set the properties, each time and in all places where you setup the SDK. It is recommended, that you do the SDK initialization in a single place.

## Feedback
If you have any questions, feedback, bugs to report, please do not hesitate contacting dev@keyper.io