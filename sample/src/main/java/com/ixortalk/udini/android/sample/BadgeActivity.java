package com.ixortalk.udini.android.sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.ixortalk.udini.android.sdk.UdiniBadge;
import com.ixortalk.udini.android.sdk.UdiniDevice;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@SuppressLint("Registered")
@EActivity(R.layout.badge)
public class BadgeActivity extends SampleActivity {

    @ViewById ListView listView;

    @Extra UdiniBadge badge;

    private Adapter adapter;

    @AfterViews
    void afterViews() {
        setActionBar(badge.name(), true);
        adapter = new Adapter();
        adapter.setNotifyOnChange(false);
        adapter.addAll(badge.devices());
        listView.setAdapter(adapter);

        dataModel.getDiscoveredDevices().observe(this, devices -> adapter.updateDiscoveredDevices(devices));
        dataModel.getCurrentDevice().observe(this, device -> adapter.refresh());
        dataModel.getLoggedIn().observe(this, loggedIn -> {
            if (!loggedIn) {
                BadgeListActivity_.intent(this)
                        .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .start();
            }
        });
    }

    private class Adapter extends ArrayAdapter<UdiniDevice> {
        private List<UdiniDevice> discoveredDevices = new ArrayList<>();

        Adapter() {
            super(BadgeActivity.this, android.R.layout.simple_list_item_1);
        }

        void refresh() {
            notifyDataSetChanged();
        }

        void updateDiscoveredDevices(List<UdiniDevice> devices) {
            this.discoveredDevices = devices;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final TextAndProgressView view = TextAndProgressView_.build(BadgeActivity.this);
            final UdiniDevice device = adapter.getItem(position);
            //noinspection ConstantConditions
            view.textView.setText(device.name());
            view.progressBar.setVisibility(device.equals(dataModel.getCurrentDevice().getValue()) ? VISIBLE : GONE);
            view.setBackgroundColor(ResourcesCompat.getColor(getResources(), discoveredDevices.contains(device) ? R.color.springgreen : R.color.watermelon, null));
            view.setOnClickListener(v -> ActionListActivity_.intent(BadgeActivity.this)
                    .badge(badge)
                    .device(device)
                    .start());
            return view;
        }
    }

}
