package com.example.sipo.sala3graff.Gallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sipo.sala3graff.FilePath;
import com.example.sipo.sala3graff.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TambahGallery extends ActionBarActivity {

    Toolbar toolbar;
    ImageView imagegallery;
    Button btngambargallery,btnsimpangallery;

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    String namagambar = "";
    TextView cekupload;
    private String URLupload = "http://sivipovo.ml/Uploadgallery.php";
    ProgressDialog dialog;
    private static final String TAG = TambahGallery.class.getSimpleName();
    private String selectedFilePath;
    String check,spotid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_gallery);
        toolbar = (Toolbar)findViewById(R.id.awesomeToolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        check = getIntent().getExtras().getString("CheckT");

        imagegallery = (ImageView)findViewById(R.id.imageViewgallery);
        btngambargallery = (Button)findViewById(R.id.btnimagegallery);
        btnsimpangallery=(Button)findViewById(R.id.btnsimpangallery);
        cekupload = (TextView)findViewById(R.id.textViewupload);

        btngambargallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PilihGambar();
            }
        });

        btnsimpangallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SimpanGallery();
                if(imagegallery.getDrawable()==null){
                    Toast.makeText(getBaseContext(), "Gambar dipilih dulu!", Toast.LENGTH_SHORT).show();
                }else {
                    dialog = ProgressDialog.show(TambahGallery.this, "", "Uploading File...", true);
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
    }

    private void SimpanGallery() {

        if(cekupload.getText().toString().equals("File Upload completed")) {
            String gambar = "http://sivipovo.ml/Gallery/"+namagambar;
            String method = "simpangallery";
            if (check.equals("gallery")){
                BackgroundTaskGallery backgroundTaskGallery = new BackgroundTaskGallery(this);
                backgroundTaskGallery.execute(method, gambar);
            }else if (check.equals("spot")){
                spotid = getIntent().getExtras().getString("spotid");
                Log.v("JAJAL ",spotid);
                BackgroundTaskGallerySpot backgroundTaskGallerySpot = new BackgroundTaskGallerySpot(this);
                backgroundTaskGallerySpot.execute(method, gambar,spotid);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Upload dulu!",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tambah_gallery, menu);
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
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imagegallery.setImageBitmap(bitmap);

                /*if(selectedFilePath != null && imagegallery.getDrawable()!= null){
                    //dialog = ProgressDialog.show(TambahGallery.this,"","Uploading File...",true);

                    *//*new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            //uploadFile(selectedFilePath);
                        }
                    }).start();*//*
                }else{
                    Toast.makeText(TambahGallery.this,"Please choose a File First",Toast.LENGTH_SHORT).show();
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void PilihGambar(){
        if(imagegallery.getDrawable() == null) {
            Intent  intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
                            SimpanGallery();
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
                        Toast.makeText(TambahGallery.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(TambahGallery.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(TambahGallery.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();

            return serverResponseCode;
        }

    }
}
