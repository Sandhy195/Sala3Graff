package com.example.sipo.sala3graff.ModulCrud;

/**
 * Created by SIPO on 9/26/2016.
 */
public class Config {

    //Alamat URL tempat kita meletakkan script PHP di PC Server

    public static final String URL_GET_ALLspot = "http://sivipovo.ml/readspot.php";
    public static final String URL_GET_ALLevent = "http://sivipovo.ml/readevent.php";
    public static final String URL_GET_ALLgallery = "http://sivipovo.ml/readgallery.php";
    public static final String URL_GET_ALLbomber = "http://sivipovo.ml/readbomber.php";
    public static final String URL_GET_ALLinfo = "http://sivipovo.ml/readinfo.php";
    public static final String URL_GET_ALLkota = "http://sivipovo.ml/readkota.php";

    public static final String URL_GET_ID = "http://sivipovo.ml/get_dataspot.php?id=";
    public static final String URL_GET_IDe = "http://sivipovo.ml/get_dataevent.php?id=";
    public static final String URL_GET_IDg = "http://sivipovo.ml/get_datagallery.php?spotid=";
    public static final String URL_GET_IDk = "http://sivipovo.ml/get_datakota.php?id=";

    // Link untuk Update data
    public static final String URL_UPDATE_SPOT="http://sivipovo.ml/updatespot.php";
    public static final String URL_UPDATE_EVENT="http://sivipovo.ml/updateevent.php";

    // Link Untuk Hapus Data
    public static final String URL_DELETE_SPOT="http://sivipovo.ml/deletespot.php?id=";
    public static final String URL_DELETE_EVENT="http://sivipovo.ml/deleteevent.php?id=";
    public static final String URL_DELETE_GALLERY="http://sivipovo.ml/deletegallery.php?id=";
    public static final String URL_DELETE_BOMBER="http://sivipovo.ml/deletebomber.php?id=";


    // Filed yang digunakan untuk dikirimkan ke Database, sesuaikan saja dengan Field di Tabel Mahasiswa
    // Update spot
    public static final String KEY_SPOT_ID = "id";
    public static final String KEY_SPOT_INFO = "info";
    public static final String KEY_SPOT_ALAMAT = "alamat";

    // Update event
    public static final String KEY_EVENT_ID = "id";
    public static final String KEY_EVENT_TANGGAL = "tanggal";
    public static final String KEY_EVENT_INFO = "info";

    // Tags Format JSON
    public static final String TAG_JSON_ARRAY="result";

    // SPOT
    public static final String TAG_ID = "id";
    public static final String TAG_GAMBARs = "gambar";
    public static final String TAG_INFO = "info";
    public static final String TAG_ALAMAT = "alamat";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_IDKOTA = "idkota";

    // EVENT
    public static final String TAG_IDe = "id";
    public static final String TAG_GAMBARe = "gambar";
    public static final String TAG_TANGGAL = "tanggal";
    public static final String TAG_INFOe = "info";

    // GALLERY
    public static final String TAG_IDg = "id";
    public static final String TAG_GAMBARg = "gambar";
    public static final String TAG_SPOTGAMBAR = "spotid";

    // BOMBER
    public static final String TAG_IDb = "id";
    public static final String TAG_GAMBARb = "gambar";
    public static final String TAG_NAMA = "nama";

    // BOMBER
    public static final String TAG_ISTILAH = "istilah";
    public static final String TAG_ARTI = "arti";

    // KOTA
    public static final String TAG_IDk = "id";
    public static final String TAG_KOTA = "kota";
    public static final String TAG_PROVINSI = "provinsi";

    //get by id
    public static final String SPOT_ID = "id";
    public static final String EVENT_ID = "id";
    public static final String GALLERY_ID = "id";
    public static final String BOMBER_ID = "id";


    //////////////////////////////////

    //Firebase app url



}
