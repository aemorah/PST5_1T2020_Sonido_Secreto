package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartsound.model.GuardadoUsuario;
import com.example.smartsound.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IngresoGuest extends AppCompatActivity {
    private List<String> listaDispo= new ArrayList<>();
    ArrayAdapter<String> arrayAdapterDispo;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> matches;
    //Parametros del reconocimiento de voz
    TextView tv;
    private ImageView image;
    private TextView text;
    private static final int RECOGNIZER_RESULT =1;
    ListView listViewDispo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_guest);
        tv=findViewById(R.id.tvTitle);
        listViewDispo=findViewById(R.id.listDispo);
        inicializarFirebase();
        databaseReference.child(GuardadoUsuario.parent).child("Usuarios").child(GuardadoUsuario.usuarioUsando).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Persona p= snapshot.getValue(Persona.class);
                assert p != null;
                tv.setText("Bienvenido "+ p.getNombre());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listarDatos();



        //Aqui empieza lo del reconocimiento de voz
        image=findViewById(R.id.imageView);
        text=findViewById(R.id.textVoz);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speachIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speachIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(speachIntent,RECOGNIZER_RESULT);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==RECOGNIZER_RESULT && resultCode== RESULT_OK){
            matches=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            databaseReference.child(GuardadoUsuario.parent).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    text.setText(matches.get(0).toLowerCase());
                    Persona p= snapshot.child("Usuarios").child(GuardadoUsuario.usuarioUsando).getValue(Persona.class);
                    assert p != null;
                    String palabraClave = p.getContrasenaDispositivo().trim();
                    if (palabraClave.equalsIgnoreCase(matches.get(0).trim())) {
                        snapshot.child("Dispositivos").child("Comedor").getRef().setValue("on");
                    }else{
                        Toast.makeText(IngresoGuest.this, "Ingreso Incorrecto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //System.out.println(matches.get(0));
//prueba de commit

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void listarDatos() {
        databaseReference.child(GuardadoUsuario.parent).child("Dispositivos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDispo.clear();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    String etiqueta= objSnapchot.getKey();
                    listaDispo.add(etiqueta);

                    arrayAdapterDispo = new ArrayAdapter<String>(IngresoGuest.this,android.R.layout.simple_list_item_1,listaDispo);
                    listViewDispo.setAdapter(arrayAdapterDispo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

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

    public void cerrar(View view){
        //System.out.println(GuardadoUsuario.parent);
        databaseReference.child(GuardadoUsuario.parent).child("Dispositivos").child("Comedor").setValue("off");
    }

}