package com.ixortalk.udini.android.sample;

import android.annotation.SuppressLint;
import android.app.Application;
import android.net.Uri;

import com.ixortalk.udini.android.sdk.UdiniApp;
import com.ixortalk.udini.android.sdk.UdiniOptions;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

/**
 * Copyright © 2019 ixor. All rights reserved.
 *
 * @author <a href="mailto:wouter.zoons@ixor.be">Wouter</a> on 2019-08-20.
 */
@EApplication
@SuppressLint("Registered")
public class SampleApplication extends Application {

    @Bean UdiniApp udini;
    @Bean DataModel dataModel;
    @Bean DataModelCallback callback;

    @AfterInject
    public void afterInject() {
        dataModel.getLoggedIn().postValue(udini.getUserProfile() != null);

        final UdiniOptions options = udini.getOptions().toBuilder()
                .setAuthBaseUrl("https://identity.udini.eu")
                .setRestApiBaseUrl("https://manager.udini.eu")
                .setClientId("J5hu2Sc5epeZ5KFOpZeweCKTpO9Euaqx")
                .setClientSecret("8jL_ISMVx2xoMC-D9r4ImOTz8UzHU2ftES_4dmVPaLkKNt3smRS-XhSsJIUJh5Ov")
                .setRedirectUri(Uri.parse("udini-example://authorize/"))
                .build();
        udini.configureWithOptions(options);
        udini.registerCallback(callback);
        udini.start();
    }

    @Override
    public void onTerminate() {
        udini.unregisterCallback(callback);
        udini.stop();
        super.onTerminate();
    }
}
