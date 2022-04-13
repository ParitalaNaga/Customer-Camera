package com.example.cameraapplication;

import static okhttp3.Protocol.HTTP_1_1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PictureActivity extends AppCompatActivity {

    private Context context;
    private static int REQUEST_CODE = 100;
    private ImageView imageView;
    private TextView text;
    private Button button;
    String nameDb = "image", responseStr, authTok = "";
    private AlertDialog dialog;
    private static final String IMAGE_DIRECTORY = "/CustomImage/";
    private mySqliteDbHandler mySqliteDbHandler;
    private SQLiteDatabase sqLiteDatabase;
    File mypath;
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        context = getApplicationContext();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        imageView = findViewById(R.id.img);
        button = findViewById(R.id.button);
        imageView.setImageBitmap(MainActivity.bitmap);
        mySqliteDbHandler = new mySqliteDbHandler(this);
        // localstorageImage(MainActivity.bitmap);
        saveImage(MainActivity.bitmap);


        setView();
    }

    public String saveImage(Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        mypath = new File(directory, "profile.jpg");
        Log.d("TAG", "saveImage: " + mypath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();

    }


    private void localstorageImage(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String stringFilePath = Environment.getExternalStorageDirectory().getPath() + "/Download/";
        File file = new File(stringFilePath, Calendar.getInstance().getTimeInMillis() + ".jpeg");
        bitmap = BitmapFactory.decodeFile(String.valueOf(file));
        Log.e("TAG", "localstorageImage: " + file);
        byte[] bytesImage = bytes.toByteArray();
        boolean insert = mySqliteDbHandler.insertImage(String.valueOf(Calendar.getInstance().getTimeInMillis()), bytesImage);
        if (insert == true) {
            Toast.makeText(this, "Insert Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Data Not Saved", Toast.LENGTH_SHORT).show();
        }

    }

    private void setView() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new postRequest().execute("", "", null);

            }
        });
    }


    private class postRequest extends AsyncTask<String, String, ImageModel> {


        String url = "https://apis-az-dev.vishwamcorp.com/v2/perk_data_collection";
        OkHttpClient client;

        @Override
        protected ImageModel doInBackground(String... strings) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .protocols(Collections.singletonList(HTTP_1_1))
                    .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                    .build();
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            multipartBodyBuilder.setType(MultipartBody.FORM);
            multipartBodyBuilder.addFormDataPart("deviceos", "a");
            multipartBodyBuilder.addFormDataPart("label_name", "");
            multipartBodyBuilder.addFormDataPart("spoof_type", "");
            multipartBodyBuilder.addFormDataPart("border_type", "");
            multipartBodyBuilder.addFormDataPart("referenceId", "store_inventory");
            multipartBodyBuilder.addFormDataPart("app_id", "jukshio");
            multipartBodyBuilder.addFormDataPart("image", String.valueOf(mypath), RequestBody.create(MediaType.parse("image/jpg"), mypath));


            Request request = new Request.Builder()
                    .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .url(url)
                    .post(multipartBodyBuilder.build())
                    .build();
            try {
                Response response = client.newCall(request).execute();
                responseStr = response.body().string();
                Log.e("TAG", "doInBackground: " + responseStr);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ImageModel();
        }

        protected void onPostExecute(ImageModel imageModel) {

            LayoutInflater layoutInflater = LayoutInflater.from(PictureActivity.this);
            View dialogRootView = layoutInflater.inflate(R.layout.dialog_response, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PictureActivity.this);
            text = dialogRootView.findViewById(R.id.text);
            alertDialogBuilder.setView(dialogRootView);
            text.setText(responseStr);
            dialog = alertDialogBuilder.create();
            dialog.show();
        }
    }

}
