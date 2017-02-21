package com.example.sipo.sala3graff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.sipo.sala3graff.tabs.SlidingTabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MainActivity extends ActionBarActivity {

    Toolbar toolbar;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.awesomeToolbar);
        ////toolbar.setSubtitle("");
        // toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //TABS
        mViewPager = (ViewPager)findViewById(R.id.v_tabs);
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager(),this));

        mSlidingTabLayout=(SlidingTabLayout)findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        //mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.pinkMuda));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accentColor));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view,R.id.tv_tab);
        mSlidingTabLayout.setViewPager(mViewPager);

        /*ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);*/

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null && !networkInfo.isConnected()){
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
            alertDialogBuilder
                    .setTitle("Could Not Connect")
                    .setMessage("Please check your connection and try again");

            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // Buatkan method hapus data kemudian dipanggil disini
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        //FirebaseMessaging.getInstance().subscribeToTopic("test");
        //FirebaseInstanceId.getInstance().getToken();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.infoo) {
            startActivity(new Intent(this, ActivityInfo.class));
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
