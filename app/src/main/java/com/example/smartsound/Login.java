package com.example.smartsound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsound.model.GuardadoUsuario;
import com.example.smartsound.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//clase del activity para el ingreso de sesion.
public class Login extends AppCompatActivity {
    //se define las variables a usarse.
    EditText et1, et2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String dato1, contra;
    String ingPass,ingreUser;

    //metodo oncreate para inicializar componentes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et1=findViewById(R.id.logUser);
        et2=findViewById(R.id.logPass);
        //se inicia la base de datos
        inicializarFirebase();
    }

    //metodo para moverse a la actividad de registro
    public void registro(View view){
        Intent i = new Intent(this, MainActivity.class );
        startActivity(i);


    }

    //metodo para el ingreso al menu, ya sea admin o user
    public void ingresar(View view){
        ingreso();
    }


    //metodo que valida que tipo de usuario se esta usando, admin o user
    private void ingreso(){
        ingreUser= et1.getText().toString().trim().toLowerCase();
        ingPass= et2.getText().toString().trim();
        if(!ingreUser.equals("") && !ingPass.equals("")) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int comprobador = 0;
                    //si el user esta en la branch principal, entonces es un administrador
                    if (snapshot.hasChild(ingreUser) ) {
                        Persona p = snapshot.child(ingreUser).child("Datos").getValue(Persona.class);
                        dato1 = p.getUsuario();
                        contra = p.getClave();
                        System.out.println(contra);
                        System.out.println(dato1);
                        if (ingPass.equals(contra)) {
                            et1.setText("");
                            et2.setText("");
                            //se agregran los datos a la clase estatica y inicia la actividad
                            GuardadoUsuario.usuarioUsando = dato1;
                            Intent i = new Intent(Login.this, MenuIngreso.class);
                            startActivity(i);
                        } else
                            Toast.makeText(Login.this, "Contraseña incorrecta",
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        for (DataSnapshot objSnapchot : snapshot.getChildren()) {
                            //se verifica si existe dentro de los usuarios padres para saber si se trata del hijo
                            if (objSnapchot.hasChild("Usuarios/" + ingreUser)) {
                                System.out.println("paso");
                                Persona p = objSnapchot.child("Usuarios").child(ingreUser).getValue(Persona.class);
                                dato1 = p.getUsuario();
                                contra = p.getClave();
                                comprobador = 1;
                                if (ingPass.equals(contra)) {
                                    et1.setText("");
                                    et2.setText("");
                                    //se agregan los datos a la clase estatica
                                    GuardadoUsuario.usuarioUsando = dato1;
                                    GuardadoUsuario.parent = objSnapchot.getKey();
                                    Intent i = new Intent(Login.this, IngresoGuest.class);
                                    startActivity(i);
                                } else
                                    Toast.makeText(Login.this, "Contraseña incorrecta",
                                            Toast.LENGTH_SHORT).show();
                            }
                        }
                        //Condicion para saber si no encontro el usuario ingresado en la base de datos
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
                et2.setError("Se requiere la Contraseña");
            }
        }

    }

    //metodo para inicializar la base de datos.
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }




}