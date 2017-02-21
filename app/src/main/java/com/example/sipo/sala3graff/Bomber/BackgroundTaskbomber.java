package com.example.sipo.sala3graff.Bomber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by SIPO on 9/28/2016.
 */
public class BackgroundTaskbomber extends AsyncTask<String,Void,String> {
    Context context;
    ProgressDialog loading;

    BackgroundTaskbomber(Context c){
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
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        TambahBomber tambahBomber = (TambahBomber)context;
        tambahBomber.startActivity(new Intent(context,MainActivity.class));
        tambahBomber.finish();

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        String urlbomber = "http://sivipovo.ml/createbomber.php";
        String method = params[0];

        //EVENT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (method.equals("simpanbomber")){
            String gambar = params[1];
            String nama = params[2];

            try {
                URL url = new URL(urlbomber);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream OS = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("gambar", "UTF-8")+"="+URLEncoder.encode(gambar,"UTF-8")+"&"+
                        URLEncoder.encode("nama","UTF-8")+"="+URLEncoder.encode(nama,"UTF-8");
                Log.v("data bomber ",data.toString());
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
