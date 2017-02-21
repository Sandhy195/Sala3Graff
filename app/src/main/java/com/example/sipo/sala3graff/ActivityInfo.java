package com.example.sipo.sala3graff;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.sipo.sala3graff.ModulCrud.Config;
import com.example.sipo.sala3graff.ModulCrud.RequestHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ActivityInfo extends ActionBarActivity {

    ImageView imageViewinfo;
    TextView textViewinfo,textViewistilah,textViewarti;
    ImageLoader imageLoader;
    ListView listView;
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_info);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.awesomeToolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageViewinfo = (ImageView)findViewById(R.id.imageViewinfo);
        textViewinfo = (TextView)findViewById(R.id.textViewinfo);
        listView = (ListView)findViewById(R.id.listviewInfo);
        textViewinfo.setText("Graffiti\n" +
                "Graffiti adalah kegiatan seni rupa yang menggunakan komposisi warna, garis, bentuk dan volume untuk menuliskan kalimat tertentu di atas dinding. Alat yang digunakan biasanya cat semprot kaleng atau pilok.");

        imageLoader.getInstance().init(UILConfig());
        imageLoader.getInstance().displayImage("http://sivipovo.ml/Picture/3.jpg",imageViewinfo);


        getJSON();

    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getActivity(),"Pengambilan Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                JSON_STRING = s;
                // Panggil method tampil data
                TampilData();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                // Proses nya sesuai alamat URL letak script PHP yang kita set di Class Config.java
                String s = rh.sendGetRequest(Config.URL_GET_ALLinfo);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void TampilData(){
        // Data dalam bentuk Array kemudian akan kita ubah menjadi JSON Object
        JSONObject jsonObject = null;
        final ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            // FOR untuk ambil data
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                // TAG_ID dan TAG_NAME adalah variabel yang ada di Class Config.java,
                String istilah = jo.getString(Config.TAG_ISTILAH);
                String arti = jo.getString(Config.TAG_ARTI);

                HashMap<String,String> info = new HashMap<>();
                info.put(Config.TAG_ISTILAH,istilah);
                info.put(Config.TAG_ARTI,arti);
                list.add(info);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_LONG).show();
        }

        ListAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if(convertView == null){
                    convertView = inflater.inflate(R.layout.iteminfo,parent,false);
                }
                TextView textViewistilah = (TextView) convertView.findViewById(R.id.textViewistilah);
                TextView textViewarti = (TextView) convertView.findViewById(R.id.textViewarti);

                textViewistilah.setText(list.get(position).get(Config.TAG_ISTILAH));
                textViewarti.setText(list.get(position).get(Config.TAG_ARTI));
                return convertView;
            }
        };
        // Tampilkan dalam bentuk ListView
        listView.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_info, menu);
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
}
