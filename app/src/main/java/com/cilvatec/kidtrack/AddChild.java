package com.cilvatec.kidtrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cilvatec.kidtrack.helper.Progress;
import com.cilvatec.kidtrack.session.GetProfile;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cilvatec.kidtrack.helper.Names.Server.ADDKID;

public class AddChild extends AppCompatActivity {

    private String picturepath;
    private Progress progress;
    private GetProfile getProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress = new Progress(this,"","Please wait saving child..");
        getProfile = new GetProfile(this);
        AppPermission();
    }

    public void BrowsePic(View view) {
        ChoosePicture();
    }

    private void AppPermission() {
        ArrayList<String> AppPerms = new ArrayList<>();
        boolean ask = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AppPerms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            ask = true;

        }
        if (ask)
            ActivityCompat.requestPermissions(this, AppPerms.toArray(new String[0]), 110);
    }
    public void ChoosePicture() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // show y i need this

                Toast.makeText(this, "please allow permissions", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 110);
            } else {
                //please unblock

                Toast.makeText(this, "please allow permissions", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 110);
            }
        } else {
            Intent choosepicture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosepicture, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 200:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String fullpath = c.getString(columnIndex);
                    picturepath = fullpath;
                    Glide.with(this)
                            .load(picturepath)
                            .centerCrop()
                            .into((AppCompatImageView) findViewById(R.id.icon));
                    c.close();
                }
                break;

        }
    }

    public void save(View view) {
        progress.ShowDialogue();
        Toast.makeText(this, ""+picturepath+"\n"+getProfile.postId()+"\n"+((EditText)findViewById(R.id.childName)).getText().toString()
                +"\n"+
                ((EditText)findViewById(R.id.activationCode)).getText().toString(), Toast.LENGTH_SHORT).show();
        Ion.with(this)
                .load(ADDKID)
                .setMultipartFile("extension",new File(picturepath))
                .setMultipartParameter("userId",getProfile.postId())
                .setMultipartParameter("name",((EditText)findViewById(R.id.childName)).getText().toString())
                .setMultipartParameter("activationCode",((EditText)findViewById(R.id.activationCode)).getText().toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progress.DismisDialogue();
                        if (e==null){
                            Toast.makeText(AddChild.this, result, Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.getBoolean("status")){

                                    Map<String, String> data = new HashMap<>();
                                    data.put("childName", ((EditText)findViewById(R.id.childName)).getText().toString());
                                    data.put("lastLocation", "Not Sync yet");
                                    data.put("kidPic", jsonObject.getString("kidPic"));
                                    data.put("childId", jsonObject.getString("childId"));

                                    MainActivity.jsonArray.put(new JSONObject(data));
                                    MainActivity.adapter.notifyItemInserted( MainActivity.jsonArray.length());
                                    finish();
                                    Toast.makeText(AddChild.this, "Child saved", Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(AddChild.this, "Error try again later", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                });
    }
}
