package com.uncmorfi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.uncmorfi.about.AboutDialog;
import com.uncmorfi.balance.BalanceFragment;
import com.uncmorfi.counter.CounterFragment;
import com.uncmorfi.map.MapFragment;
import com.uncmorfi.menu.MenuFragment;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int EXIT_INTERVAL_TIME = 2000;
    private double mLastBackPressed;
    private Toast mExitToast;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            Fragment firstFragment = new MenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, firstFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mLastBackPressed + EXIT_INTERVAL_TIME > System.currentTimeMillis()) {
            mExitToast.cancel();
            super.onBackPressed();
        } else {
            mLastBackPressed = System.currentTimeMillis();
            mExitToast = Toast.makeText(this, getString(R.string.press_back_again),
                    Toast.LENGTH_SHORT);
            mExitToast.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_menu:
                fragment = new MenuFragment();
                break;
            case R.id.nav_balance:
                fragment = new BalanceFragment();
                break;
            case R.id.nav_counter:
                fragment = new CounterFragment();
                break;
            case R.id.nav_map:
                MapFragment mapFragment = new MapFragment();
                fragment = mapFragment;

                mapFragment.getMapAsync(this);
                break;
            case R.id.nav_about:
                new AboutDialog().show(getSupportFragmentManager(), "NewUserDialog");
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(item.getTitle());
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapFragment fragment = (MapFragment)
                getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (fragment != null) {
            fragment.onMapReady(googleMap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}