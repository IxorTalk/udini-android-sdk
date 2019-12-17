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

import com.ixortalk.udini.android.sdk.UdiniAction;
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
@EActivity(R.layout.badgelist)
public class ActionListActivity extends SampleActivity {

    @ViewById ListView listView;

    @Extra UdiniBadge badge;
    @Extra UdiniDevice device;

    private Adapter adapter;

    @AfterViews
    void afterViews() {
        setActionBar(device.name(), true);
        adapter = new Adapter();
        adapter.addAll(device.actions());
        listView.setAdapter(adapter);

        dataModel.getDiscoveredDevices().observe(this, devices -> adapter.updateDiscoveredDevices(devices));
        dataModel.getCurrentDevice().observe(this, device -> adapter.refresh());
        dataModel.getCurrentAction().observe(this, action -> adapter.refresh());

        dataModel.getLoggedIn().observe(this, loggedIn -> {
            if (!loggedIn) {
                BadgeListActivity_.intent(this)
                        .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .start();
            }
        });
    }

    private class Adapter extends ArrayAdapter<UdiniAction> {
        private List<UdiniDevice> discoveredDevices = new ArrayList<>();

        Adapter() {
            super(ActionListActivity.this, android.R.layout.simple_list_item_1);
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
            final TextAndProgressView view = TextAndProgressView_.build(ActionListActivity.this);
            final UdiniAction action = adapter.getItem(position);
            //noinspection ConstantConditions
            view.textView.setText(action.name());
            view.setBackgroundColor(ResourcesCompat.getColor(getResources(), discoveredDevices.contains(device) ? R.color.springgreen : R.color.watermelon, null));
            view.setOnClickListener(v -> udini.invokeAction(badge, device, action));

            final boolean isDevice = device.equals(dataModel.getCurrentDevice().getValue());
            final boolean isAction = action.equals(dataModel.getCurrentAction().getValue());
            view.progressBar.setVisibility(isDevice && isAction ? VISIBLE : GONE);

            return view;
        }
    }
}
