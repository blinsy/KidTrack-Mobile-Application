package com.cilvatec.kidtrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cilvatec.kidtrack.helper.Progress;
import com.cilvatec.kidtrack.session.GetProfile;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twirry.twirrylibrary.Twirry;
import com.twirry.twirrylibrary.task.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

import static com.cilvatec.kidtrack.helper.Names.Server.VIEWMYKIDS;

public class MainActivity extends AppCompatActivity {
    public static JSONArray jsonArray;
    public static RecyclerView.Adapter adapter;
    private SimpleLocation simpleLocation;
    private Progress progress;
    String childId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress=new Progress(this,"","please wait loading ....");
        simpleLocation = new SimpleLocation(this);
        simpleLocation.setBlurRadius(10);
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddChild.class));
            }
        });

        jsonArray = new JSONArray();

        Twirry.SetContext(this)
                .Load(jsonArray)
                .setSingleLayout(R.layout.unit_child,true)
                .dataMapTextView(R.id.childName, "childName")
                .dataMapTextView(R.id.lastLocation, "lastLocation")
                .dataMapTextView(R.id.phone, "phone")
                .dataMapImageView(R.id.icon, "kidPic")
                .IntoRecyclerView(R.id.RecyclerView)
                .getClickEvents(new Event() {
                    @Override
                    public void onClick(View view, int i) {
                        String uri = null;
                        try {
                            uri = "http://maps.google.com/maps?saddr=" + simpleLocation.getLatitude() + "," + simpleLocation.getLongitude() + "&daddr=" + jsonArray.getJSONObject(i).getString("latitude") + "," + jsonArray.getJSONObject(i).getString("longitude");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);

                    }

                    @Override
                    public void onLongClick(View view, int i) {

                    }

                    @Override
                    public void onBindItem(View view, final int i) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.delete);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, ":"+i, Toast.LENGTH_SHORT).show();
                                jsonArray.remove(i);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                });
        adapter=((RecyclerView)findViewById(R.id.RecyclerView)).getAdapter();

        AppPermission();
    }

    private void AppPermission() {
        ArrayList<String> AppPerms = new ArrayList<>();
        boolean ask = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AppPerms.add(Manifest.permission.ACCESS_FINE_LOCATION);
            ask = true;
        } else {
            simpleLocation.beginUpdates();
            fetchLocations();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AppPerms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            ask = true;

        }
        if (ask)
            ActivityCompat.requestPermissions(this, AppPerms.toArray(new String[0]), 110);
    }
    private void fetchLocations(){
        progress.ShowDialogue();
        Map<String,String>data = new HashMap<>();
        data.put("slo",""+simpleLocation.getLongitude());
        data.put("sla",""+simpleLocation.getLatitude());

        Ion.with(this)
                .load(VIEWMYKIDS)
                .setStringBody(new JSONObject(data).toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progress.DismisDialogue();
                        if (e==null){
//                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                            try {
                                JSONArray kids =   new JSONArray(result);
                                for (int i = 0; i < kids.length(); i++) {
                                    jsonArray.put(kids.getJSONObject(i));
                                }
                                new JSONArray(result);
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
}
