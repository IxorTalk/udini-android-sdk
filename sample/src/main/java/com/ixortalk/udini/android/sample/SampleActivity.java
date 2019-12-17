package com.ixortalk.udini.android.sample;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ixortalk.udini.android.sdk.UdiniApp;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

/**
 * Copyright © 2019 ixor. All rights reserved.
 *
 * @author <a href="mailto:wouter.zoons@ixor.be">Wouter</a> on 2019-08-20.
 */
@EActivity
public abstract class SampleActivity extends AppCompatActivity {

    @Bean UdiniApp udini;
    @Bean DataModel dataModel;

    private AlertDialog currentDialog;

    @AfterInject
    void afterBaseInject() {
        dataModel.getDialogMessage().observe(this, errorMessage -> {
            if (currentDialog != null) {
                return;
            }
            currentDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", (dialog, which) -> currentDialog = null)
                    .show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // make sure Udini is scanning, udini might not scan when at a certain point the list
        // of badges would be empty
        udini.start();
    }

    protected void setActionBar(String title, boolean homeAsUp) {
        setTitle(title);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(homeAsUp);
            actionBar.setDisplayHomeAsUpEnabled(homeAsUp);
            actionBar.setDisplayShowHomeEnabled(homeAsUp);
            actionBar.setDisplayShowTitleEnabled(title != null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
