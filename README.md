
# Udini SDK for Android user guide

This user guide describes the components that make up the Udini SDK for Android, and how to
use them when building your own applications.

Note that there also is a sample app that shows a basic implementation show-casing the most common
features a typically Udini client implementation will provide.

## What is Udini?

Udini is an access control tool that allows users to authenticate themselves against physical
hardware devices in order to have access granted to them. This access might come in the form of
a door that unlocks, a light that is turned on, an audio recording that is played. There is
no inherent limit to the types of applications that can make use of it.

## SDK for Android

Udini is a distributed system where the authentication and authorization is controlled by a central
node. The SDK for Android is a library that allows communication with this node. This allows
Android client applications to present a user interface while delegating the Udini specific
logic to the library while retaining focus on user experience and optionally other core business
requirements.

Note that there also is an SDK for iOS, it will not be discussed in this user guide.

## Architecture

THe SDK consists of several building blocks with each having a well-defined set of responsibilities, 
effectively implementing a clean separation of concerns. The main components that can be identified
are:

    - SDK access-point: the main entry point for clients to use the Udini SDK

    - Event bus: type-safe communication between the SDK's components for Android

    - Bluetooth control: everything related to access to the on-board BLE chip
    
    - Networking support: proxied network calls to the Udini server
    
    - Security: keypair generation and management of access tokens
    
    - Persistence: storage of any type of data

This user guide will mainly focus on the first component, since it is the only one client 
implementations will need to use directly. However, it is important to understand how the other
components are integrated because there are Android specific features and constraints that will
influence the behavior of certain features within the SDK.

### Implementation

The Udini SDK runs as a service once started. When the client application would exit or be killed
by the OS the service will continue to run. This is important when it is required to have Udini
scan for devices in the background, or to continue scanning after a system restart.

Because of this decoupling it is important to have a means of disconnecting and reconnecting a 
client from and to the SDK, this is done via an event bus onto which the client registers itself.
This way a registered client will automatically receive updates on changes of the internal state
of the SDK. The client is free to handle these updates any way it likes. Most commonly a data
model will be kept up-to-date, while client components in turn observe changes to that data model
directly. 

### How to integrate

In order to integrate the Udini SDK for Android you will need to take care of the points as 
described in the following points. In its simplest form your Udini client application will
initialize the Udini SDK using some configuration properties and listen for state changes
in order to determine what to show to the user.

#### Have your application initialize the Udini SDK

The first thing to do is have your application start the Udini SDK so that the service is running
in the background. You can do this by first setting the configuration parameters and next calling
`UdiniApp.start()`.

The SDK will not be able to function without proper access to the central online service that 
implements the authentication details. Online access is implemented via OAuth2, so in order to
make sure you have access will need:

    - a client id that uniquely identifies your clients to the server, this is a simple string
      such as `MyCompanyUdiniAndroidClient`
    
    - a client secret that is associated to the client id, this is a type of password, something 
      like `RcdD34wPt5b6TTg3`
    
    - a redirect URI that will be called on the device when the OAuth2 flow returns, e.g.
      `myclient://authorize/`. Note that on Android, unlike iOS, the redirect URI requires a 
      trailing slash. The custom scheme is used to ensure that Android will never attempt to
      propose to open a web browser when returning from the OAuth2 flow

```kotlin
    val udini = UdiniApp.sharedInstance(this)

    override fun onCreate() {
        super.onCreate()
        val options = udini.options.toBuilder()
            .setClientId("MyCompanyUdiniAndroidClient")
            .setClientSecret("RcdD34wPt5b6TTg3")
            .setRedirectUri("myclient://authorize/")
            .build()
        udini.configureWithOptions(options)
        udini.registerCallback(udiniCallback)
        udini.start()
    }
    
    // when implementing using an Activity you can unregister this way
    override fun destroy() {
        udini.unregisterCallback(udiniCallback)
        super.onDestroy()
    }
```

#### Receive updates of state changes within the Udini SDK

Once the Udini SDK has been initialized you will want to start receiving updates on the current
state. That way you will be able to keep the user interface in sync with the data in de SDK.

The `udiniCallback` variable in the above snippet is an implementation of 
`com.ixortalk.udini.android.sdk.UdiniCallback`, most commonly you would in this case simply
extend the more convenient `com.ixortalk.udini.android.sdk.UdiniCallbackAdapter`, that way
you would only need to override the callback methods you are interested in.
Note that it is allowed to register multiple callbacks at the same time, they will all receive
updates until they have been unregistered.

Note that when adding the Udini SDK as a dependency to your Android project it will merge the 
following `AndroidManifest.xml` (only interesting parts are shown):

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-feature
        android:name="android.hardware.bluetooth_le"
        android:required="false" />
</manifest>
```

The most common callback to receive is `onUdiniLoginRequired()`. The proper way to respond to this
is to invoke `UdiniApp.login(Context context)`. Doing so will trigger an Udini-specific activity to 
be launched in which the OAuth2 flow is started. Recording authentication state and communication
with the server is all handled by the SDK. You will only need to worry about receiving
`onUdiniLoginSuccessful()` and/or `onUdiniLoginFailed(UdiniException exception, int errorCode)`.

For a more detailed example of an Udini client implementation please check out the sample 
application.
