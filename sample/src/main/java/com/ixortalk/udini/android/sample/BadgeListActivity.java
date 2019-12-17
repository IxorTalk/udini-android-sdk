package com.ixortalk.udini.android.sample;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ixortalk.udini.android.sdk.UdiniBadge;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

@RuntimePermissions
@SuppressLint("Registered")
@EActivity(R.layout.badgelist)
@OptionsMenu(R.menu.badgelist_menu)
public class BadgeListActivity extends SampleActivity {

    @ViewById ListView listView;

    private Adapter adapter;
    private boolean loggedIn;

    @AfterViews
    void afterViews() {
        setActionBar("Badges", false);
        adapter = new Adapter();
        adapter.setNotifyOnChange(false);
        listView.setAdapter(adapter);

    }

    @OptionsMenuItem(R.id.loginLogoutMenuItem)
    void injectMenuItem(MenuItem item) {
        this.loggedIn = dataModel.getLoggedIn().getValue();
        item.setTitle(loggedIn ? "Logout" : "Login");

        dataModel.getLoggedIn().observe(this, loggedIn -> {
            this.loggedIn = loggedIn;
            item.setTitle(loggedIn ? "Logout" : "Login");
        });
    }

    @Override
    @NeedsPermission(ACCESS_FINE_LOCATION)
    protected void onStart() {
        super.onStart();
        udini.getBadges(false, (badges, http, error) -> updateBadges(badges));
    }

    @UiThread
    void updateBadges(List<UdiniBadge> badges) {
        adapter.clear();
        if (badges != null) {
            adapter.addAll(badges);
        }
        adapter.notifyDataSetChanged();
    }

    @OptionsItem(R.id.loginLogoutMenuItem)
    void onLoginLogoutClicked() {
        if (loggedIn) {
            udini.logout(this);
        } else {
            udini.login(this);
        }
    }

    private class Adapter extends ArrayAdapter<UdiniBadge> {
        Adapter() {
            super(BadgeListActivity.this, android.R.layout.simple_list_item_1);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final TextView view = (TextView)super.getView(position, convertView, parent);
            final UdiniBadge badge = adapter.getItem(position);
            //noinspection ConstantConditions
            view.setText(badge.name());
            view.setOnClickListener(v -> BadgeActivity_.intent(BadgeListActivity.this)
                    .badge(badge)
                    .start());
            return view;
        }
    }
}
