package com.example.smartsound;

import com.google.firebase.database.FirebaseDatabase;

public class FireBase extends android.app.Application{
    @Override
    public void onCreate() {
        //Clase para el vinculo de la base de datos
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
