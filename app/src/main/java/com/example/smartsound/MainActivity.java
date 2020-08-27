package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingreso,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.icon_add:{
                    Toast.makeText(this,"Agregar",Toast.LENGTH_SHORT).show();
                    break;
            }
            case R.id.icon_save: {
                Toast.makeText(this, "Guardar", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.icon_delete: {
                Toast.makeText(this, "Borrar", Toast.LENGTH_SHORT).show();
                break;
            }
            default:break;
        }

        return true;
    }
}