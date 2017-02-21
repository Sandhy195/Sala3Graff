package com.example.sipo.sala3graff.Spot;


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
import com.example.sipo.sala3graff.MapsActivity;
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
public class SpotFrag extends android.support.v4.app.Fragment{


    View view;
    ListView listViewspot;
    FloatingActionButton fab;
    private String JSON_STRING;
    String ids;

    ImageLoader imageLoader;

    public SpotFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_spot,container,false);
        listViewspot =(ListView)view.findViewById(R.id.listViewspot);
        fab = (FloatingActionButton)view.findViewById(R.id.fabspot);
        fab.attachToListView(listViewspot);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MapsActivity.class));

            }
        });
        //fab.show();

        imageLoader.destroy();
        getJSON();

        listViewspot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(),""+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),DetailSpot.class);
                HashMap<String,String> map = (HashMap)parent.getItemAtPosition(position);

                String spotId = map.get(Config.TAG_ID).toString();
                intent.putExtra(Config.SPOT_ID,spotId);
                intent.putExtra("Check","Detail");
                startActivity(intent);

            }
        });

        listViewspot.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map = (HashMap)parent.getItemAtPosition(position);
                ids = map.get(Config.TAG_ID).toString();
                confirmManipulasi();
                return true;
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
                String s = rh.sendGetRequest(Config.URL_GET_ALLspot);
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
                String id = jo.getString(Config.TAG_ID);
                String gambar = jo.getString(Config.TAG_GAMBARs);
                String info = jo.getString(Config.TAG_INFO);
                String alamat = jo.getString(Config.TAG_ALAMAT);
                String latitude = jo.getString(Config.TAG_LATITUDE);
                String longitude = jo.getString(Config.TAG_LONGITUDE);

                HashMap<String,String> spot = new HashMap<>();
                spot.put(Config.TAG_ID,id);
                spot.put(Config.TAG_GAMBARs,gambar);
                spot.put(Config.TAG_INFO,info);
                spot.put(Config.TAG_ALAMAT,alamat);
                spot.put(Config.TAG_LATITUDE,latitude);
                spot.put(Config.TAG_LONGITUDE,longitude);
                list.add(spot);
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
                    convertView = inflater.inflate(R.layout.itemspot,parent,false);
                }
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewIspot);
                TextView textViewinfo = (TextView) convertView.findViewById(R.id.textView2Ispotinfo);
                TextView textViewalamat = (TextView) convertView.findViewById(R.id.textView3Ispotalamat);

                imageLoader.getInstance().displayImage(list.get(position).get(Config.TAG_GAMBARs),imageView);

                textViewinfo.setText(list.get(position).get(Config.TAG_INFO));
                textViewalamat.setText(list.get(position).get(Config.TAG_ALAMAT));
                return convertView;
            }
        };
        // Tampilkan dalam bentuk ListView
        listViewspot.setAdapter(adapter);
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

    private void confirmManipulasi(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Manipulasi Data..");

        alertDialogBuilder.setPositiveButton("DELETE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Buatkan method hapus data kemudian dipanggil disini
                        confirmDelete();

                    }
                });

        alertDialogBuilder.setNegativeButton("EDIT",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(getActivity(),DetailSpot.class);
                        String spotId = ids;
                        intent.putExtra(Config.SPOT_ID,spotId);
                        intent.putExtra("Check","Edit");
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                getJSON();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_DELETE_SPOT, ids);
                return s;
            }
        }

        DeleteData de = new DeleteData();
        de.execute();
    }


}
