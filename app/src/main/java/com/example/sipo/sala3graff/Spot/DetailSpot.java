package com.example.sipo.sala3graff.Spot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sipo.sala3graff.Gallery.TambahGallery;
import com.example.sipo.sala3graff.MainActivity;
import com.example.sipo.sala3graff.MapsSpot;
import com.example.sipo.sala3graff.ModulCrud.Config;
import com.example.sipo.sala3graff.ModulCrud.RequestHandler;
import com.example.sipo.sala3graff.R;
import com.example.sipo.sala3graff.modulMap.GPSTracker;
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

import java.util.ArrayList;
import java.util.HashMap;


public class DetailSpot extends ActionBarActivity {

    FloatingActionButton fabmap,fabedit,fabtambah;
    ImageView imageDspot;
    TextView textinfoDspot,textalamatDspot,textkota;
    EditText txtinfo,txtalamat;
    String id,latitude,longitude,check,idkota;
    GridView gridView;

    GPSTracker gps;
    double mylat,mylong;

    ImageLoader imageLoader;
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_spot);
        Intent intent = getIntent();
        id = intent.getStringExtra(Config.SPOT_ID);
        check = getIntent().getExtras().getString("Check");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.awesomeToolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fabmap = (FloatingActionButton)findViewById(R.id.fabDspot);
        fabedit = (FloatingActionButton)findViewById(R.id.fabDedit);
        fabtambah = (FloatingActionButton)findViewById(R.id.fabtambahG);

        gridView = (GridView)findViewById(R.id.gridViewgalleryspot);
        imageDspot = (ImageView)findViewById(R.id.imageViewDspot);
        textinfoDspot = (TextView)findViewById(R.id.editTextDspotinfo);
        textalamatDspot = (TextView)findViewById(R.id.editTextDspotalamat);
        txtinfo = (EditText)findViewById(R.id.txtinfo);
        txtalamat = (EditText)findViewById(R.id.txtalamat);
        textkota = (TextView)findViewById(R.id.textViewDkota);

        if (check.equals("Detail")){
            txtinfo.setVisibility(View.GONE);
            txtalamat.setVisibility(View.GONE);
            fabedit.setVisibility(View.GONE);
        }else if (check.equals("Edit")){
            toolbar.setTitle("Edit Spot");
            textinfoDspot.setVisibility(View.GONE);
            textalamatDspot.setVisibility(View.GONE);
        }

        getData();


        imageLoader.getInstance().init(UILConfig());

        fabedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();

            }
        });

        fabmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toLokasi();
            }
        });

        fabtambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailSpot.this,TambahGallery.class);
                String spotid = id;
                i.putExtra("spotid",spotid);
                i.putExtra("CheckT","spot");
                startActivity(i);
                Log.v("CEK = ",spotid);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_spot, menu);
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
        class getData extends AsyncTask<Void,Void,String>{
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(DetailSpot.this,"Proses Data...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                ShowData(s);
                getDatakota();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();
                String s = requestHandler.sendGetRequestParam(Config.URL_GET_ID,id);
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
            String gambar = c.getString(Config.TAG_GAMBARs);
            String info = c.getString(Config.TAG_INFO);
            String alamat = c.getString(Config.TAG_ALAMAT);
            latitude = c.getString(Config.TAG_LATITUDE);
            longitude = c.getString(Config.TAG_LONGITUDE);
            idkota = c.getString(Config.TAG_IDKOTA);

            // Tampilkan setiap data JSON format kedalam setiap EditText
            imageLoader.getInstance().displayImage(gambar,imageDspot);
            textinfoDspot.setText(info);
            textalamatDspot.setText(alamat);

            txtinfo.setText(info);
            txtalamat.setText(alamat);
           // Toast.makeText(getBaseContext(),latitude+","+longitude,Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateData(){
        final String info = txtinfo.getText().toString().trim();
        final String alamat = txtalamat.getText().toString().trim();

        class UpdateData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailSpot.this,"Update Data...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(DetailSpot.this, s, Toast.LENGTH_SHORT).show();
                if (!loading.isShowing()){
                    startActivity(new Intent(DetailSpot.this,MainActivity.class));
                    finish();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_SPOT_ID,id);
                hashMap.put(Config.KEY_SPOT_INFO,info);
                hashMap.put(Config.KEY_SPOT_ALAMAT,alamat);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.URL_UPDATE_SPOT,hashMap);

                return s;
            }
        }

        UpdateData ue = new UpdateData();
        ue.execute();
    }

    private void toLokasi(){
        gps = new GPSTracker(DetailSpot.this);

        if (gps.canGetLocation()){

            mylat = gps.getLatitude();
            mylong = gps.getLongitude();

            Intent i = new Intent(DetailSpot.this,MapsSpot.class);
            i.putExtra("mylat",mylat);
            i.putExtra("mylong",mylong);
            i.putExtra("lat",latitude);
            i.putExtra("long",longitude);
            startActivity(i);

            Toast.makeText(getBaseContext(),"lat= "+mylat+",long= "+mylong,Toast.LENGTH_SHORT).show();
        }else {
            gps.showSettingsAlert();
        }
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

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                TampilData();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_IDg,id);
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
            for(int i = result.length()-1; i>=0; i--){
                JSONObject jo = result.getJSONObject(i);
                // TAG_ID dan TAG_NAME adalah variabel yang ada di Class Config.java,
                String id = jo.getString(Config.TAG_IDg);
                String gambar = jo.getString(Config.TAG_GAMBARg);
                String spotid = jo.getString(Config.TAG_SPOTGAMBAR);

                HashMap<String,String> event = new HashMap<>();
                event.put(Config.TAG_IDg,id);
                event.put(Config.TAG_GAMBARg,gambar);
                event.put(Config.TAG_SPOTGAMBAR,spotid);
                list.add(event);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
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
                    convertView = inflater.inflate(R.layout.itemgallery,parent,false);
                }
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewIgallery);

                imageLoader.getInstance().displayImage(list.get(position).get(Config.TAG_GAMBARg),imageView);

                return convertView;
            }
        };
        // Tampilkan dalam bentuk ListView
        gridView.setAdapter(adapter);
    }


    private void getDatakota(){
        class getData extends AsyncTask<Void,Void,String>{
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ShowDatakota(s);
                getJSON();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler requestHandler = new RequestHandler();
                String s = requestHandler.sendGetRequestParam(Config.URL_GET_IDk,idkota);
                return s;
            }
        }
        getData gd = new getData();
        gd.execute();
    }

    private void ShowDatakota(String json){
        try {
            // Jadikan sebagai JSON object
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            // Data berdasarkan di Tabel Database
            String kota = c.getString(Config.TAG_KOTA);
            String provinsi = c.getString(Config.TAG_PROVINSI);
            // Tampilkan setiap data JSON format kedalam setiap EditText
            textkota.setText(kota);
            // Toast.makeText(getBaseContext(),latitude+","+longitude,Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
