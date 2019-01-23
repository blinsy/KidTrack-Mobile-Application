package com.cilvatec.kidtrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.cilvatec.kidtrack.helper.Progress;
import com.cilvatec.kidtrack.session.GetProfile;
import com.cilvatec.kidtrack.session.SetProfile;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cilvatec.kidtrack.helper.Names.Server.LOGIN;

public class Login extends AppCompatActivity {
private Progress progress;
    private GetProfile getProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress = new Progress(this,"","please wait loading..");
        getProfile = new GetProfile(this);
        if (getProfile.isLoggedIn()){
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Login(View view) {
        progress.ShowDialogue();
        Map<String,String>data=new HashMap<>();
        data.put("email",((EditText)findViewById(R.id.et_email)).getText().toString());
        data.put("password",((EditText)findViewById(R.id.et_password)).getText().toString());


        Ion.with(this)
                .load(LOGIN)
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
                                    SetProfile setProfile = new SetProfile(Login.this);
                                    setProfile.setLoggedIn(true);
                                    setProfile.setPostId(jsonObject.getString("postId"));
                                    setProfile.Save();
                                startActivity(new Intent(Login.this,MainActivity.class));
                                finish();
                                }else {
                                    Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                Toast.makeText(Login.this, "Check Network"+result, Toast.LENGTH_SHORT).show();
                            }



                        }else {
                            Toast.makeText(Login.this, "Check Network", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void Signup(View view) {
        startActivity(new Intent(this,Signup.class));
        finish();
    }
}
