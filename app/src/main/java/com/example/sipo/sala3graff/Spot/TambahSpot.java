package com.example.sipo.sala3graff.Spot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sipo.sala3graff.FilePath;
import com.example.sipo.sala3graff.MainActivity;
import com.example.sipo.sala3graff.ModulCrud.Config;
import com.example.sipo.sala3graff.R;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Handler;


public class TambahSpot extends ActionBarActivity {

    Toolbar toolbar;
    ImageView imagespot;
    Button btngambarspot,btnsimpanspot;
    EditText txtinfospot,txtalamatspot;

    private Spinner spinner;
    private ArrayList<String> kota;
    private JSONArray result;
    String idkota;

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    String latitude,longitude,address;

    String namagambar = "";
    TextView cekupload;
    private String URLupload = "http://sivipovo.ml/Uploadspot.php";
    ProgressDialog dialog;
    private static final String TAG = TambahSpot.class.getSimpleName();
    private String selectedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_spot);

        toolbar = (Toolbar)findViewById(R.id.awesomeToolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imagespot = (ImageView)findViewById(R.id.imageViewspot);
        btngambarspot = (Button)findViewById(R.id.btnimagespot);
        btnsimpanspot=(Button)findViewById(R.id.btnsimpanspot);
        txtinfospot =(EditText)findViewById(R.id.editTextinfospot);
        txtalamatspot =(EditText)findViewById(R.id.editText2alamatspot);
        cekupload = (TextView)findViewById(R.id.textViewupload);



        kota = new ArrayList<String>();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idkota = getidKota(position);
                Log.v("CEK IDKOTA ",idkota);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getDatakota();

        ceklokasi();
        //txtalamatspot.setText(address);

        btnsimpanspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SimpanSpot();
                if(txtinfospot.getText().toString().equals("")||txtalamatspot.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(),"Gambar,Info dan alamat diisi dulu!",Toast.LENGTH_SHORT).show();
                }else {
                    dialog = ProgressDialog.show(TambahSpot.this, "", "Uploading File...", true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            uploadFile(selectedFilePath);
                        }
                    }).start();
                }

            }
        });

        btngambarspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PilihGambar();
            }
        });

    }

    private void getDatakota(){
        StringRequest stringRequest = new StringRequest(Config.URL_GET_ALLkota, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray(Config.TAG_JSON_ARRAY);
                    getKota(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getKota(JSONArray j){
        for (int i=0; i<j.length();i++){
            try {
                JSONObject jsonObject = j.getJSONObject(i);
                kota.add(jsonObject.getString(Config.TAG_KOTA));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,kota);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private String getidKota(int position){
        String idkota = "";
        JSONObject jsonObject = null;
        try {
            jsonObject = result.getJSONObject(position);
            idkota = jsonObject.getString(Config.TAG_IDk);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return idkota;
    }

    private void ceklokasi() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            latitude = bundle.getString("latitude");
            longitude = bundle.getString("longitude");
            //address = bundle.getString("address");
        }
        //Log.v("adrsss ",address.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tambah_spot, menu);
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

    private void SimpanSpot() {
        if(cekupload.getText().toString().equals("File Upload completed")) {

            String info = txtinfospot.getText().toString();
            String alamat = txtalamatspot.getText().toString();
            String gambar = "http://sivipovo.ml/Spot/"+namagambar;
            String method = "simpanspot";
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(method, gambar, info, alamat, latitude, longitude,idkota);
        }else {
            Toast.makeText(getApplicationContext(),"Upload dulu!",Toast.LENGTH_SHORT).show();
        }

    }
    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void PilihGambar(){
        if(imagespot.getDrawable() == null) {
            Intent  intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {

                namagambar = getRealPathFromURI(filePath);
                namagambar = namagambar.substring(namagambar.lastIndexOf("/")+1,namagambar.length());

                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i(TAG,"Selected File Path:" + selectedFilePath);

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imagespot.setImageBitmap(bitmap);

                /*if(selectedFilePath != null && imagespot.getDrawable()!= null){
                    dialog = ProgressDialog.show(TambahSpot.this,"","Uploading File...",true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            uploadFile(selectedFilePath);
                        }
                    }).start();
                }else{
                    Toast.makeText(TambahSpot.this,"Please choose a File First",Toast.LENGTH_SHORT).show();
                }*/

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //android upload file to server
    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                    Toast.makeText(getApplicationContext(),"Source File Doesn't Exist: " + selectedFilePath,Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(URLupload);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "File Upload completed.",Toast.LENGTH_SHORT).show();
                            cekupload.setText("File Upload completed");
                            SimpanSpot();
                        }
                    });
                }else {
                    cekupload.setText("Gagal Upload");
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TambahSpot.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(TambahSpot.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(TambahSpot.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();

            return serverResponseCode;
        }

    }
}
