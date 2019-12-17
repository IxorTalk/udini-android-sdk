package com.ixortalk.udini.android.sample;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hadilq.liveevent.LiveEvent;
import com.ixortalk.udini.android.sdk.UdiniAction;
import com.ixortalk.udini.android.sdk.UdiniDevice;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Copyright © 2019 ixor. All rights reserved.
 *
 * @author <a href="mailto:wouter.zoons@ixor.be">Wouter</a> on 2019-08-20.
 */
@EBean(scope = Singleton)
class DataModel {

    private final MutableLiveData<List<UdiniDevice>> discoveredDevices = new MutableLiveData<>(new ArrayList<>());

    private final LiveEvent<String> dialogMessage = new LiveEvent<>();
    private final LiveEvent<Boolean> loggedIn = new LiveEvent<>();
    private final LiveEvent<UdiniDevice> currentDevice = new LiveEvent<>();
    private final LiveEvent<UdiniAction> currentAction = new LiveEvent<>();

    LiveData<List<UdiniDevice>> getDiscoveredDevices() {
        return discoveredDevices;
    }

    void addDiscoveredDevice(UdiniDevice device) {
        final List<UdiniDevice> oldList = discoveredDevices.getValue();
        final List<UdiniDevice> newList = new ArrayList<>(oldList.size() + 1);
        newList.addAll(oldList);
        newList.add(device);
        discoveredDevices.postValue(newList);
    }

    void removeDiscoveredDevice(UdiniDevice device) {
        final List<UdiniDevice> oldList = discoveredDevices.getValue();
        final List<UdiniDevice> newList = new ArrayList<>(oldList);
        newList.remove(device);
        discoveredDevices.postValue(newList);
    }

    LiveEvent<String> getDialogMessage() {
        return dialogMessage;
    }

    LiveEvent<Boolean> getLoggedIn() {
        return loggedIn;
    }

    LiveEvent<UdiniDevice> getCurrentDevice() {
        return currentDevice;
    }

    LiveEvent<UdiniAction> getCurrentAction() {
        return currentAction;
    }
}
