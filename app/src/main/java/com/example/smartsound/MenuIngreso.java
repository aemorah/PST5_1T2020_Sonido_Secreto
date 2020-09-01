package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.smartsound.model.GuardadoUsuario;
import com.example.smartsound.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//actividad de menu de ingreso del administrador
public class MenuIngreso extends AppCompatActivity {
    //inicializacion de variables
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView tv1;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_ingreso);
        inicializarFirebase();

        tv1=findViewById(R.id.tituloBien);
        //se hace un pequeño query para obtener el nombre y imprimirlo
        databaseReference.child(GuardadoUsuario.usuarioUsando).child("Datos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Persona p= snapshot.getValue(Persona.class);
                assert p != null;
                tv1.setText("Bienvenido "+ p.getNombre());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //metodo de inicializacion de la base de datos
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    //metodo para cambiar el layout del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //metodo para dar acciones a los iconos en el menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.icon_exit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log Out");
            builder.setIcon(R.drawable.edit_aviso);
            builder.setMessage("Seguro quiere salir?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return true;
    }

    //metodo para ir al activity donde se desbloquea la puerta
    public void hablar(View view){
        Intent i = new Intent(this, DesbloquearPuerta.class );
        startActivity(i);

    }

    //metodo para administrar los dispositivos
    public void adminDispo(View view){
        Intent i = new Intent(this, AdministrarDispo.class );
        startActivity(i);

    }


    //metodo para ir a la activity de registrar usuarios
    public void registraUser(View view){
        Intent i = new Intent(this, RegistrarUsuario.class );
        startActivity(i);


    }

    //metodo para ir a la activity donde se administran los usuarios.
    public void adminUser(View view){
        Intent i = new Intent(this, AdministrarUsuarios.class );
        startActivity(i);
    }

}