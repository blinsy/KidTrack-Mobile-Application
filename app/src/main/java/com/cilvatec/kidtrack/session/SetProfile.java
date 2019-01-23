package com.cilvatec.kidtrack.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.cilvatec.kidtrack.helper.Names;


/**
 * Created by chris on 9/19/2016. for placefinder
 */
public class SetProfile {
    private SharedPreferences.Editor editor;
    public SetProfile(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Names.Session.SESSION, Context.MODE_PRIVATE);
        this.editor= sharedPreferences.edit();
    }
    public void setLoggedIn(Boolean LoggedIn) {
        editor.putBoolean(Names.Session.LOGGEDIN,LoggedIn);
    }
    public void setPostId(String postId) {
        editor.putString(Names.Session.POSTID,postId);
    }
    public void setCompletedProfile(Boolean CompletedProfile) {
        editor.putBoolean(Names.Session.COMPLETEDPROFILE,CompletedProfile);
    }
    public void setConfirmed(Boolean Confirmed){
        editor.putBoolean(Names.Session.CONFERMED,Confirmed);
    }
    public void Save(){
        editor.apply();
    }
}
