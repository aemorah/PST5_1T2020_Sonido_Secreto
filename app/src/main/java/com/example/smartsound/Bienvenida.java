package com.example.smartsound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

//clase splash, que se usa para mostrar el logo por una cantidad de tiempo determinada
public class Bienvenida extends AppCompatActivity {
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvendia);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Bienvenida.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 3500); //en el delay se puede definir el tiempo del activity.
    }
}