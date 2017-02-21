package com.example.sipo.sala3graff.Event;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sipo.sala3graff.MainActivity;
import com.example.sipo.sala3graff.ModulCrud.Config;
import com.example.sipo.sala3graff.ModulCrud.RequestHandler;
import com.example.sipo.sala3graff.R;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailEvent extends ActionBarActivity {

    FloatingActionButton fabedit;
    ImageView imageDevent;
    TextView textinfoDevent,txttanggal;
    EditText txtinfo;
    String id,tanggal,check;
    Calendar calendar = Calendar.getInstance();

    ImageLoader imageLoader;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        Intent intent = getIntent();
        id = intent.getStringExtra(Config.EVENT_ID);
        check = getIntent().getExtras().getString("Check");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.awesomeToolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fabedit = (FloatingActionButton)findViewById(R.id.fabDedit);

        imageDevent = (ImageView)findViewById(R.id.imageViewDevent);
        textinfoDevent = (TextView)findViewById(R.id.editTextDeventinfo);
        txttanggal = (TextView)findViewById(R.id.txttanggal);
        txtinfo = (EditText)findViewById(R.id.txteinfo);

        if (check.equals("Detail")){
            txtinfo.setVisibility(View.GONE);
            fabedit.setVisibility(View.GONE);
        }else if (check.equals("Edit")){
            toolbar.setTitle("Edit Event");
            textinfoDevent.setVisibility(View.GONE);
            txttanggal.setVisibility(View.GONE);
        }

        getData();

        imageLoader.getInstance().init(UILConfig());

        fabedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        class getData extends AsyncTask<Void,Void,String> {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(DetailEvent.this,"Proses Data...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                ShowData(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();
                String s = requestHandler.sendGetRequestParam(Config.URL_GET_IDe,id);
                return s;
            }
        }
        getData gd = new getData();
        gd.execute();
    }

    private void ShowData(String json){
        try {
            // Jadikan sebagai JSON object
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            // Data berdasarkan di Tabel Database
            String gambar = c.getString(Config.TAG_GAMBARe);
            String info = c.getString(Config.TAG_INFOe);
            String tanggal = c.getString(Config.TAG_TANGGAL);
            // Tampilkan setiap data JSON format kedalam setiap EditText
            imageLoader.getInstance().displayImage(gambar,imageDevent);
            txttanggal.setText(tanggal);
            textinfoDevent.setText(info);
            txtinfo.setText(info);
            // Toast.makeText(getBaseContext(),latitude+","+longitude,Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cektanggal() {
        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tanggal = simpleDateFormat.format(calendar.getTime());
    }

    private void updateData(){
        final String info = txtinfo.getText().toString().trim();
        cektanggal();
        class UpdateData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailEvent.this,"Update Data...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(DetailEvent.this, s, Toast.LENGTH_SHORT).show();
                if (!loading.isShowing()){
                    startActivity(new Intent(DetailEvent.this,MainActivity.class));
                    finish();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_EVENT_ID,id);
                hashMap.put(Config.KEY_EVENT_TANGGAL,tanggal);
                hashMap.put(Config.KEY_EVENT_INFO,info);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.URL_UPDATE_EVENT,hashMap);

                return s;
            }
        }

        UpdateData ue = new UpdateData();
        ue.execute();
    }

    private ImageLoaderConfiguration UILConfig(){
        final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)  //cache #1
                .cacheOnDisk(true) //cache #2
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(android.R.drawable.ic_dialog_alert)
                .showImageOnFail(android.R.drawable.stat_notify_error)
                .considerExifParams(true) //cache #3
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) //fill_width #5
                .build();

        ////cache #4
        //add <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> to manifest
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        //end of cache 4
        return config;
    }
}
