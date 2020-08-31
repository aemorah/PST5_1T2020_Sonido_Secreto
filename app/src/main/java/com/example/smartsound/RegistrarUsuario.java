package com.example.smartsound;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegistrarUsuario extends AppCompatActivity {
    private List<Persona> listaPersonas= new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;


    EditText user, nombre, apellido, correo, password, celular,contraDispo;
    ListView listViewPersonas;
    String valorUser, valorNom, valorCorreo, valorContra, valorApellido, valorCelu, valorPassDispo;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        user=findViewById(R.id.usuario);
        nombre=findViewById(R.id.nom);
        apellido=findViewById(R.id.apellido);
        correo=findViewById(R.id.correo);
        password=findViewById(R.id.contra);
        celular=findViewById(R.id.celular);
        contraDispo=findViewById(R.id.contraDis);

        listViewPersonas=findViewById(R.id.muestra);
        inicializarFirebase();
        listarDatos();

        listViewPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personaSel= (Persona) adapterView.getItemAtPosition(i);
                user.setText(personaSel.getUsuario());
                nombre.setText(personaSel.getNombre());
                apellido.setText(personaSel.getApellidos());
                password.setText(personaSel.getClave());
                correo.setText(personaSel.getCorreo());
                celular.setText(personaSel.getTelefono());
                contraDispo.setText(personaSel.getContrasenaDispositivo());
            }
        });

    }
    private void listarDatos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPersonas.clear();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    Persona p= objSnapchot.getValue(Persona.class);
                    listaPersonas.add(p);

                    arrayAdapterPersona = new ArrayAdapter<>(RegistrarUsuario.this,android.R.layout.simple_list_item_1,listaPersonas);
                    listViewPersonas.setAdapter(arrayAdapterPersona);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingreso,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private boolean validaciones(){
        valorUser= user.getText().toString().toLowerCase().trim();
        valorNom=nombre.getText().toString().trim();
        valorCorreo=correo.getText().toString().trim();
        valorContra= password.getText().toString().trim();
        valorApellido= apellido.getText().toString().trim();
        valorCelu=celular.getText().toString().trim();
        valorPassDispo = contraDispo.getText().toString().trim();
        if (valorUser.equals("") || valorNom.equals("") || valorCorreo.equals("") ||
                valorContra.equals("")|| valorApellido.equals("")|| valorCelu.equals("") ||
                valorPassDispo.equals("")){
            if (valorUser.equals(""))
                user.setError("Se requiere el Usuario");
            if (valorNom.equals(""))
                nombre.setError("Se requiere el Nombre");
            if (valorCorreo.equals(""))
                correo.setError("Se requiere el Correo");
            if (valorContra.equals(""))
                password.setError("Se requiere la Contraseña");
            if (valorApellido.equals(""))
                apellido.setError("Se requiere el Apellido");
            if (valorCelu.equals(""))
                celular.setError("Se requiere el Celular");
            if (valorPassDispo.equals(""))
                contraDispo.setError("Se requiere el Contraseña para el dispositivo");
            return false;
        }

        else
            return true;

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
            if (validaciones()) {
                switch (item.getItemId()) {
                    case R.id.icon_add: {
                        Persona per = new Persona();
                        //per.setPid(UUID.randomUUID().toString());
                        per.setApellidos(valorApellido);
                        per.setClave(valorContra);
                        per.setCorreo(valorCorreo);
                        per.setUsuario(valorUser);
                        per.setNombre(valorNom);
                        per.setTelefono(valorCelu);
                        per.setContrasenaDispositivo(valorPassDispo);
                        databaseReference.child(GuardadoUsuario.usuarioUsando).child("Usuarios").child(per.getUsuario()).setValue(per);
                        Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                        vaciar();
                        break;
                    }
                    default:
                        break;
                }
            }


        }
        return true;

    }

    private void vaciar(){
        user.setText("");
        nombre.setText("");
        apellido.setText("");
        correo.setText("");
        password.setText("");
        celular.setText("");
        contraDispo.setText("");
    }


}