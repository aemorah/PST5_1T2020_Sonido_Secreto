package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartsound.model.GuardadoUsuario;
import com.example.smartsound.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText et1, et2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String dato1, contra;
    String ingPass,ingreUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et1=findViewById(R.id.logUser);
        et2=findViewById(R.id.logPass);
        inicializarFirebase();
    }

    public void registro(View view){
        Intent i = new Intent(this, MainActivity.class );
        startActivity(i);


    }

    public void ingresar(View view){
        ingreso();
    }

    private void ingreso(){
        ingreUser= et1.getText().toString().trim().toLowerCase();
        ingPass= et2.getText().toString().trim();
        if(!ingreUser.equals("") && !ingPass.equals("")) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //System.out.println(snapshot);
                    //System.out.println(snapshot.child(et1.getText().toString()).exists());
                    int comprobador = 0;
                    if (snapshot.child(ingreUser).exists()) {
                        Persona p = snapshot.child(ingreUser).child("Datos").getValue(Persona.class);
                        dato1 = p.getUsuario();
                        contra = p.getClave();
                        if (ingPass.equals(contra)) {
                            et1.setText("");
                            et2.setText("");
                            GuardadoUsuario.usuarioUsando = dato1;
                            Intent i = new Intent(Login.this, MenuIngreso.class);
                            startActivity(i);
                        } else
                            Toast.makeText(Login.this, "Contraseña incorrecta",
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        for (DataSnapshot objSnapchot : snapshot.getChildren()) {
                            if (objSnapchot.hasChild("Usuarios/" + ingreUser)) {
                                Persona p = objSnapchot.child("Usuarios").child(ingreUser).getValue(Persona.class);
                                dato1 = p.getUsuario();
                                contra = p.getClave();
                                comprobador = 1;
                                if (ingPass.equals(contra)) {
                                    et1.setText("");
                                    et2.setText("");
                                    GuardadoUsuario.usuarioUsando = dato1;
                                    Intent i = new Intent(Login.this, IngresoGuest.class);
                                    startActivity(i);
                                } else
                                    Toast.makeText(Login.this, "Contraseña incorrecta",
                                            Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (comprobador == 0) {
                            Toast.makeText(Login.this, "No existe un artículo con dicho Usuario",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Toast.makeText(Login.this, "Ingrese Datos Completos",
                    Toast.LENGTH_SHORT).show();
            if (ingreUser.equals("")) {
                et1.setError("Se requiere el Usuario");
            }
            if (ingPass.equals("")) {
                et2.setError("Se requiere el Contraseña");
            }
        }

    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }





}