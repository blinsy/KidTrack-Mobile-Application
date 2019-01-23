package com.cilvatec.kidtrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.cilvatec.kidtrack.helper.Progress;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.StreamBody;
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

import static com.cilvatec.kidtrack.helper.Names.Server.VIEWMYKID;

public class LocationsView extends AppCompatActivity {
    String childId;
    private Progress progress;
    private RecyclerView.Adapter adapter;
    private JSONArray jsonArray;
    private SimpleLocation simpleLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress=new Progress(this,"","please wait loading ....");
        jsonArray = new JSONArray();
        Intent  intent = getIntent();
        childId=intent.getStringExtra("childId");
        simpleLocation = new SimpleLocation(this);
        simpleLocation.setBlurRadius(10);
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }
        Twirry.SetContext(this)
                .Load(jsonArray)
                .setSingleLayout(R.layout.unit_location,true)
                .dataMapTextView(R.id.time,"time")
                .dataMapTextView(R.id.distance,"distance")
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
                    public void onBindItem(View view, int i) {

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
        Map<String,String>pos=new HashMap<>();
        pos.put("postId",childId);
        pos.put("slo",""+simpleLocation.getLongitude());
        pos.put("sla",""+simpleLocation.getLatitude());
        Ion.with(this)
                .load(VIEWMYKID)
                .setStringBody(new JSONObject(pos).toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progress.DismisDialogue();
                        if (e==null){
                            Toast.makeText(LocationsView.this, result, Toast.LENGTH_SHORT).show();
                            try {
                                JSONArray kidl = new JSONArray(result);

                                for (int i = 0; i < kidl.length(); i++) {


                                        jsonArray.put(kidl.getJSONObject(i));

                                }
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }


                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocations();
                } else {
                    Toast.makeText(this, "You must allow Location permission to see precise distance", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }
}
