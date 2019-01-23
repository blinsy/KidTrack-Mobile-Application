package com.cilvatec.kidtrack.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.cilvatec.kidtrack.helper.Names;


/**
 * Created by chris on 9/19/2016. for Twirry
 */
public class GetProfile {
    private SharedPreferences sharedPreferences;

    public GetProfile(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Names.Session.SESSION, context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Names.Session.LOGGEDIN, false);
    }

    public String postId() {
        return sharedPreferences.getString(Names.Session.POSTID,null);
    }

    public boolean Confirmed() {
        return sharedPreferences.getBoolean(Names.Session.CONFERMED, true);
    }

    public boolean CompletedProfile() {
        return sharedPreferences.getBoolean(Names.Session.COMPLETEDPROFILE, true);
    }

    public String Mobile() {
        return sharedPreferences.getString(Names.Session.MOBILENUMBER, null);
    }

    public String Name() {
        return sharedPreferences.getString(Names.Session.NAME, null);
    }
}
