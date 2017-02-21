package com.example.sipo.sala3graff.Bomber;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class BomberFrag extends android.support.v4.app.Fragment {


    View view;
    ListView listViewbomber;
    FloatingActionButton fab;
    private String JSON_STRING;
    String idb;

    ImageLoader imageLoader;
    
    public BomberFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bomber,container,false);
        listViewbomber =(ListView)view.findViewById(R.id.listViewbomber);
        
        // Inflate the layout for this fragment

        fab = (FloatingActionButton)view.findViewById(R.id.fabbomber);
        fab.attachToListView(listViewbomber);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TambahBomber.class));
            }
        });
        //fab.show();

        getJSON();

        listViewbomber.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map = (HashMap)parent.getItemAtPosition(position);
                idb = map.get(Config.TAG_IDb).toString();
                confirmDelete();
                return false;
            }
        });

        imageLoader.getInstance().init(UILConfig());

        return view;
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
                String s = rh.sendGetRequest(Config.URL_GET_ALLbomber);
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
                String id = jo.getString(Config.TAG_IDb);
                String gambar = jo.getString(Config.TAG_GAMBARb);
                String nama = jo.getString(Config.TAG_NAMA);

                HashMap<String,String> bomber = new HashMap<>();
                bomber.put(Config.TAG_IDb,id);
                bomber.put(Config.TAG_GAMBARb,gambar);
                bomber.put(Config.TAG_NAMA,nama);
                list.add(bomber);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Error!",Toast.LENGTH_LONG).show();
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
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if(convertView == null){
                    convertView = inflater.inflate(R.layout.itembomber,parent,false);
                }
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewIbomber);
                TextView textViewnama = (TextView) convertView.findViewById(R.id.textView2Ibombernama);

                imageLoader.getInstance().displayImage(list.get(position).get(Config.TAG_GAMBARb),imageView);

                textViewnama.setText(list.get(position).get(Config.TAG_NAMA));

                return convertView;
            }
        };
        // Tampilkan dalam bentuk ListView
        listViewbomber.setAdapter(adapter);
    }

    private void confirmDelete(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Apa Kamu Yakin Untuk Menghapus Data ini?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Buatkan method hapus data kemudian dipanggil disini
                        deleteData();

                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void deleteData(){
        class DeleteData extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Update Data...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                if (!loading.isShowing()){
                    startActivity(new Intent(getActivity(),MainActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_DELETE_BOMBER, idb);
                return s;
            }
        }

        DeleteData de = new DeleteData();
        de.execute();
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
                .Builder(getActivity())
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
