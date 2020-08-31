package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private  List<String> listaStatus= new ArrayList<>();
    private List<Integer> listaImg = new ArrayList<>();
    String[] arrayDis;
    String[] arraySta;
    Integer[] arrayimg;

//    ArrayAdapter<String> arrayAdapterDispo;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> matches;
    //Parametros del reconocimiento de voz
    TextView tv;

    String dispoSel;
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
        obtenerInfo();
        dispoSel="";

        listViewDispo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dispoSel= (String) adapterView.getItemAtPosition(i);
                System.out.println(dispoSel);
            }
        });



        //Aqui empieza lo del reconocimiento de voz
        image=findViewById(R.id.imageView);
        text=findViewById(R.id.textVoz);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dispoSel.equalsIgnoreCase("")) {
                    Intent speachIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    speachIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(speachIntent, RECOGNIZER_RESULT);
                }else
                    Toast.makeText(IngresoGuest.this, "Seleccione Un Dispositivo", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            databaseReference.child(GuardadoUsuario.parent).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    text.setText(matches.get(0).toLowerCase());
                    Persona p = snapshot.child("Usuarios").child(GuardadoUsuario.usuarioUsando).getValue(Persona.class);
                    assert p != null;
                    String palabraClave = p.getContrasenaDispositivo().trim();
                    if (palabraClave.equalsIgnoreCase(matches.get(0).trim())) {
                        snapshot.child("Dispositivos").child(dispoSel).child("Activacion").getRef().setValue("on");
                    } else {
                        Toast.makeText(IngresoGuest.this, "Ingreso Incorrecto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    class MyAdapter extends ArrayAdapter<String>{
        Context context;
        String[] dispositivos;
        String[] estado;
        Integer[] imagenes;

        MyAdapter(Context c, String[] dispositivos, String[] estado, Integer[] imagenes){
            super(c,R.layout.row,R.id.interTitulo,dispositivos);
            this.context=c;
            this.dispositivos=dispositivos;
            this.estado=estado;
            this.imagenes=imagenes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row= layoutInflater.inflate(R.layout.row,parent,false);
            ImageView images = row.findViewById(R.id.image);
            TextView myDispositivos = row.findViewById(R.id.interTitulo);
            TextView myStatus = row.findViewById(R.id.interStatus);

            images.setImageResource(imagenes[position]);
            myDispositivos.setText(dispositivos[position]);
            myStatus.setText(estado[position]);

            return row;
        }
    }

    private void obtenerInfo() {
        databaseReference.child(GuardadoUsuario.parent).child("Dispositivos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDispo.clear();
                listaStatus.clear();
                listaImg.clear();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    String etiqueta= objSnapchot.getKey();
                    //System.out.println(etiqueta);
                    String status= "Status: "+ objSnapchot.child("Activacion").getValue();
                    //System.out.println(status);
                    listaDispo.add(etiqueta);
                    listaStatus.add(status);
                    listaImg.add(R.drawable.edit_candado);

                    arrayDis=new String[listaDispo.size()];
                    arrayDis = listaDispo.toArray(arrayDis);
                    arraySta=new String[listaStatus.size()];
                    arraySta = listaStatus.toArray(arraySta);
                    arrayimg=new Integer[listaImg.size()];
                    arrayimg = listaImg.toArray(arrayimg);
                    MyAdapter adapter =new MyAdapter(IngresoGuest.this,arrayDis,arraySta,arrayimg);
                    listViewDispo.setAdapter(adapter);
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

    public void cerrar(View view) {
        //System.out.println(GuardadoUsuario.parent);
        if (!dispoSel.equals("")) {
            databaseReference.child(GuardadoUsuario.parent).child("Dispositivos").child(dispoSel).child("Activacion").setValue("off");
        }else
            Toast.makeText(IngresoGuest.this, "Seleccione un Dispositivo", Toast.LENGTH_SHORT).show();
    }

}