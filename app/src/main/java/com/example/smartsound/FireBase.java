package com.example.smartsound;

import com.google.firebase.database.FirebaseDatabase;

public class FireBase extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
