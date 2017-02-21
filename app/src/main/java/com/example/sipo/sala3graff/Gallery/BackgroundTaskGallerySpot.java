package com.example.sipo.sala3graff.Gallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.sipo.sala3graff.Gallery.TambahGallery;
import com.example.sipo.sala3graff.MainActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by SIPO on 10/16/2016.
 */

public class BackgroundTaskGallerySpot extends AsyncTask<String,Void,String> {
    Context context;
    ProgressDialog loading;

    BackgroundTaskGallerySpot(Context c) {
        this.context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = ProgressDialog.show(context,"Proses Kirim Data...","Wait...",false,false);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        loading.dismiss();
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        if (s!= null) {
            TambahGallery tambahGallery = (TambahGallery)context;
            tambahGallery.startActivity(new Intent(context, MainActivity.class));
            tambahGallery.finish();
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
        String urlevent = "http://sivipovo.ml/creategalleryspot.php";
        String method = params[0];

        //EVENT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (method.equals("simpangallery")){
            String gambar = params[1];
            String spotid = params[2];

            try {
                URL url = new URL(urlevent);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream OS = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("gambar", "UTF-8")+"="+URLEncoder.encode(gambar,"UTF-8")+"&"+
                        URLEncoder.encode("spotid","UTF-8")+"="+URLEncoder.encode(spotid,"UTF-8");
                Log.v("data event ", data.toString());
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();
                Log.v("datas ",httpURLConnection.toString());
                IS.close();
                return "Success simpan..";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
