package com.ixortalk.udini.android.sample;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ixortalk.udini.android.sdk.UdiniAction;
import com.ixortalk.udini.android.sdk.UdiniCallbackAdapter;
import com.ixortalk.udini.android.sdk.UdiniDevice;
import com.ixortalk.udini.android.sdk.UdiniDeviceState;
import com.ixortalk.udini.android.sdk.UdiniException;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Copyright © 2019 ixor. All rights reserved.
 *
 * @author <a href="mailto:wouter.zoons@ixor.be">Wouter</a> on 2019-08-20.
 */
@EBean(scope = Singleton)
public class DataModelCallback extends UdiniCallbackAdapter {

    private static final String TAG = DataModelCallback.class.getSimpleName();

    @Bean DataModel dataModel;

    @Override
    public void onUdiniLoginRequired() {
        dataModel.getLoggedIn().postValue(false);
    }

    @Override
    public void onUdiniLoginSuccessful() {
        dataModel.getLoggedIn().postValue(true);
    }

    @Override
    public void onUdiniLoginFailed(UdiniException exception, int errorCode) {
        Log.e(TAG, "Login failed", exception);
        dataModel.getLoggedIn().postValue(false);
        dataModel.getDialogMessage().postValue(exception.getMessage());
    }

    @Override
    public void onUdiniLogoutSuccessful() {
        dataModel.getLoggedIn().postValue(false);
    }

    @Override
    public void onUdiniDiscoveredDevice(@NonNull UdiniDevice device) {
        dataModel.addDiscoveredDevice(device);
    }

    @Override
    public void onUdiniLostDevice(@NonNull UdiniDevice device) {
        dataModel.removeDiscoveredDevice(device);
    }

    @Override
    public void onUdiniDeviceStateChange(@NonNull UdiniDevice device, @Nullable UdiniAction action, @NonNull UdiniDeviceState state) {
        Log.d(TAG, "device progress: " + state.getProgress());
        if (state.isSuccess()) {
            dataModel.getCurrentDevice().postValue(null);
            dataModel.getCurrentAction().postValue(null);
            return;
        }
        // only signal device changes
        if (!device.equals(dataModel.getCurrentDevice().getValue())) {
            dataModel.getCurrentDevice().postValue(device);
        }
        if (action == null) {
            if (dataModel.getCurrentAction().getValue() != null) {
                dataModel.getCurrentAction().postValue(null);
            }
        } else {
            if (!action.equals(dataModel.getCurrentAction().getValue())) {
                dataModel.getCurrentAction().postValue(action);
            }
        }
    }

    @Override
    public void onUdiniDeviceActionFailed(@NonNull UdiniDevice device, @Nullable UdiniAction action, @NonNull UdiniException error) {
        dataModel.getDialogMessage().postValue("Error invoking action: " + error.getMessage());
        dataModel.getCurrentDevice().postValue(null);
        dataModel.getCurrentAction().postValue(null);
    }
}
