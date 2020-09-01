package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class AdministrarUsuarios extends AppCompatActivity {
    private ListView listaUsuarios;
    String userSel;
    private List<String> listaNombres= new ArrayList<>();
    private List<String> listaDes=new ArrayList<>();
    private List<Integer> listaImg = new ArrayList<>();
    String[] arrayDis;
    String[] arrayDes;
    Integer[] arrayimg;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_usuarios);
        listaUsuarios= findViewById(R.id.listViewUsuarios);
        TextView bienvenida = findViewById(R.id.textView7);
        bienvenida.setText("Bienvenido  "+ GuardadoUsuario.usuarioUsando);
        inicializarFirebase();
        obtenerInfo();
        listaUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userSel= (String) adapterView.getItemAtPosition(i);
                System.out.println(userSel);
                System.out.println("Estoy imprimiendo la seleccion");
            }
        });

    }
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
    private void obtenerInfo() {
        databaseReference.child(GuardadoUsuario.usuarioUsando).child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaNombres.clear();
                listaDes.clear();
                listaImg.clear();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    String etiqueta= objSnapchot.getKey();

                    listaNombres.add(etiqueta);
                    listaDes.add("Nombre: "+objSnapchot.child("nombre").getValue()+" "+
                            objSnapchot.child("apellidos").getValue());
                    listaImg.add(R.drawable.ic_person);

                    arrayDis=new String[listaNombres.size()];
                    arrayDis = listaNombres.toArray(arrayDis);
                    arrayDes=new String[listaDes.size()];
                    arrayDes=listaDes.toArray(arrayDes);
                    arrayimg=new Integer[listaImg.size()];
                    arrayimg = listaImg.toArray(arrayimg);
                    MyAdapter adapter =new MyAdapter(AdministrarUsuarios.this,arrayDis,arrayDes,arrayimg);
                    listaUsuarios.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.icon_exit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.edit_aviso);
            builder.setMessage("Seguro quiere regresar?");
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

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.edit_aviso);
            builder.setMessage("Seguro quiere borrar el usuario "+ userSel+ "?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    databaseReference.child(GuardadoUsuario.usuarioUsando).child("Usuarios").child(userSel).removeValue();
                    databaseReference.child("UsersRegis").child(userSel).removeValue();
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




    //clases internas
    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] nombres;
        Integer[] imagenes;
        String[] descripciones;

        MyAdapter(Context c, String[] dispositivos,String[] descripciones, Integer[] imagenes){
            super(c,R.layout.row1,R.id.interTitulo,dispositivos);
            this.context=c;
            this.descripciones=descripciones;
            this.nombres=dispositivos;
            this.imagenes=imagenes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row1= layoutInflater.inflate(R.layout.row1,parent,false);
            ImageView images = row1.findViewById(R.id.image1);
            TextView myDispositivos = row1.findViewById(R.id.interTitulo);
            TextView myDescripciones = row1.findViewById(R.id.descripcion);
            images.setImageResource(imagenes[position]);
            myDispositivos.setText(nombres[position]);
            myDescripciones.setText(descripciones[position]);


            return row1;
        }
    }
}