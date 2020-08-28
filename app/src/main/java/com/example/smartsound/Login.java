package com.example.smartsound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void registro(View view){
        Intent i = new Intent(this, MainActivity.class );
        startActivity(i);


    }

    public void ingresar(View view){
        Intent i = new Intent(this, MenuIngreso.class );
        startActivity(i);


    }





}