package com.cilvatec.kidtrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cilvatec.kidtrack.helper.Progress;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cilvatec.kidtrack.helper.Names.Server.SIGNUP;

public class Signup extends AppCompatActivity {
private Progress progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress = new Progress(this,"","Please wailt ....");


    }

    public void Signup(View view) {
        progress.ShowDialogue();
        Map<String,String> data=new HashMap<>();
        data.put("email",((EditText)findViewById(R.id.et_email)).getText().toString());
        data.put("password",((EditText)findViewById(R.id.et_password)).getText().toString());
        Ion.with(this)
                .load(SIGNUP)
                .setStringBody(new JSONObject(data).toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progress.DismisDialogue();
                        if (e==null){

                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.getBoolean("status")){
                                    Toast.makeText(Signup.this, "Signup success", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(Signup.this,Login.class));
                                    finish();
                                }else {
                                    Toast.makeText(Signup.this, "Error"+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                Toast.makeText(Signup.this, "Check Network"+result, Toast.LENGTH_SHORT).show();
                            }



                        }else {
                            Toast.makeText(Signup.this, "Check Network", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void Login(View view) {
        startActivity(new Intent(this,Login.class));
        finish();
    }
}
