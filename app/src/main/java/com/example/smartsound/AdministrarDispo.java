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
import android.widget.EditText;
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

public class AdministrarDispo extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText ingreso1;
    String ingresoU;
    ListView listViewDispo;

    private List<String> listaDispo= new ArrayList<>();
    private  List<String> listaStatus= new ArrayList<>();
    private List<Integer> listaImg = new ArrayList<>();
    String[] arrayDis;
    String[] arraySta;
    Integer[] arrayimg;

    String dispositivoSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_dispo);
        ingreso1=findViewById(R.id.ingresoDispositivo);
        listViewDispo=findViewById(R.id.listaDispo);

        inicializarFirebase();
        obtenerInfo();
        listViewDispo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dispositivoSel= (String) adapterView.getItemAtPosition(i);
                System.out.println(dispositivoSel);
            }
        });
    }


    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    class MyAdapter extends ArrayAdapter<String> {
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
        databaseReference.child(GuardadoUsuario.usuarioUsando).child("Dispositivos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDispo.clear();
                listaStatus.clear();
                listaImg.clear();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    String etiqueta= objSnapchot.getKey();
                    String status= "Status: "+ objSnapchot.child("Activacion").getValue();
                    listaDispo.add(etiqueta);
                    listaStatus.add(status);
                    listaImg.add(R.drawable.edit_candado);

                    arrayDis=new String[listaDispo.size()];
                    arrayDis = listaDispo.toArray(arrayDis);
                    arraySta=new String[listaStatus.size()];
                    arraySta = listaStatus.toArray(arraySta);
                    arrayimg=new Integer[listaImg.size()];
                    arrayimg = listaImg.toArray(arrayimg);
                    MyAdapter adapter =new MyAdapter(AdministrarDispo.this,arrayDis,arraySta,arrayimg);
                    listViewDispo.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_dispo,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.icon_exit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.edit_aviso);
            builder.setMessage("'¿Seguro quiére regresar?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
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
            switch (item.getItemId()) {
                case R.id.icon_add: {
                    if (validacionIngreso()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.edit_aviso);
                        builder.setMessage("¿Quiére agregregar el dispositivo "+ ingresoU + "?");
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                databaseReference.child(GuardadoUsuario.usuarioUsando).child("Dispositivos").child(ingresoU).child("Activacion").setValue("off");
                                databaseReference.child(GuardadoUsuario.usuarioUsando).child("Dispositivos").child(ingresoU).child("Estado").setValue("no");
                                ingreso1.setText("");
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
                        Toast.makeText(this, "Ingrese un Dispositivo", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case R.id.icon_delete: {
                    if (!dispositivoSel.equals("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.edit_aviso);
                        builder.setMessage("¿Quiére borrar el dispositivo "+ dispositivoSel+"?");
                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                databaseReference.child(GuardadoUsuario.usuarioUsando).child("Dispositivos").child(dispositivoSel).removeValue();
                                dispositivoSel="";
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
                        Toast.makeText(this, "Seleccione un Dispositivo que Desee Borrar", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
        return true;

    }

    private boolean validacionIngreso(){
        ingresoU = ingreso1.getText().toString();
        return !ingresoU.trim().equals("");

    }



}