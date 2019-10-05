package com.example.mopgyphi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mopgyphi.Helper.roomHelper.AppDatabase;
import com.veyo.autorefreshnetworkconnection.CheckNetworkConnectionHelper;
import com.veyo.autorefreshnetworkconnection.listener.StopReceiveDisconnectedListener;

import java.io.File;
import java.util.List;

import androidx.room.Room;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements TrendingHomeFragment.OnFragmentInteractionListener, GifOpenedFragment.OnFragmentInteractionListener {
    public static int navigationItemId;
    private static final int SDCARD_PERMISSION = 1;
    public static boolean online = true;
    public static AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        checkStoragePermission();
        getSupportActionBar().hide();
        if (savedInstanceState == null) {
            setStartingFragment();
        } else {
            // do nothing - fragment is recreated automatically
        }


        db= Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().fallbackToDestructiveMigration().build();



        /*CheckNetworkConnectionHelper
                .getInstance()
                .registerNetworkChangeListener(new StopReceiveDisconnectedListener() {
                    @Override
                    public void onDisconnected() {
                        //Do your task on Network Disconnected!
                        Toasty.error(MainActivity.this, "Provjerite vašu konekciju!", Toast.LENGTH_SHORT, true).show();
                        online=false;
                    }

                   *//* @Override
                    public void onNetworkConnected() {
                        //Do your task on Network Connected!
                        Log.e(TAG, "onConnected");

                    }*//*

                    @Override
                    public Context getContext() {
                        return MainActivity.this;
                    }
                });*/
        //setStartingFragment();
}

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Write permission is required so that folder picker can create new folder.
            //If you just want to pick files, Read permission is enough.

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        SDCARD_PERMISSION);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setStartingFragment() {
        android.support.v4.app.Fragment fragment=null;
        Class fragmentClass = TrendingHomeFragment.class;

        if(fragmentClass!=null)
        {
            try {
                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentReplaceLayout,fragment).commit();
        }
        navigationItemId=R.id.trendingHomeFragment;
    }

    @Override
    public void onFragmentInteraction(String GifURL) {
        GifOpenedFragment newFragment = new GifOpenedFragment();
        Bundle args = new Bundle();
        args.putString("url", GifURL);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragmentReplaceLayout, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
        navigationItemId=R.id.fragmentGifOpened;
    }

    @Override
    public void onFragmentInteraction() {

    }
    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();


        if(navigationItemId==R.id.trendingHomeFragment){
            finish();
            System.exit(0);
        }

        else if (fragments.size()>0){
            setStartingFragment();
        }
        else {
            super.onBackPressed();
        }
    }

}

