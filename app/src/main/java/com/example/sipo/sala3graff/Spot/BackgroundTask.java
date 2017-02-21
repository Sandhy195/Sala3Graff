package com.example.sipo.sala3graff.Spot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.sipo.sala3graff.MainActivity;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by SIPO on 9/27/2016.
 */
public class BackgroundTask extends AsyncTask<String,Void,String> {
    Context context;
    ProgressDialog loading;

    BackgroundTask(Context c){
        this.context = c;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = ProgressDialog.show(context,"Proses Kirim Data...","Wait...",false,false);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        loading.dismiss();
        if (result!= null) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            TambahSpot tambahSpot = (TambahSpot) context;
            tambahSpot.startActivity(new Intent(context, MainActivity.class));
            tambahSpot.finish();
        }else {
            Toast.makeText(context, "Gagal Kirim Data", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {

        String urlspot = "http://sivipovo.ml/createspot.php";
        String method = params[0];

        int status =0;

        //SPOT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (method.equals("simpanspot")){
            String gambar = params[1];
            String info = params[2];
            String alamat = params[3];
            String latitude = params[4];
            String longitude = params[5];
            String idkota = params[6];

            try {

                URL url = new URL(urlspot);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream OS = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
            String data =
                    URLEncoder.encode("gambar","UTF-8")+"="+URLEncoder.encode(gambar,"UTF-8")+"&"+
                    URLEncoder.encode("info","UTF-8")+"="+URLEncoder.encode(info,"UTF-8")+"&"+
                    URLEncoder.encode("alamat","UTF-8")+"="+URLEncoder.encode(alamat,"UTF-8")+"&"+
                    URLEncoder.encode("latitude","UTF-8")+"="+URLEncoder.encode(latitude,"UTF-8")+"&"+
                    URLEncoder.encode("longitude","UTF-8")+"="+URLEncoder.encode(longitude, "UTF-8")+"&"+
                    URLEncoder.encode("idkota","UTF-8")+"="+URLEncoder.encode(idkota, "UTF-8");
                Log.v("data ",data.toString());

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();
                Log.v("datas ",httpURLConnection.toString());
                IS.close();
                return "Success simpan";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


}
