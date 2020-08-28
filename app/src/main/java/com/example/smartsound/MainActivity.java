package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.smartsound.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private List<Persona> listaPersonas= new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;


    EditText user, nombre, apellido, correo, password, celular;
    ListView listViewPersonas;
    String valorUser, valorNom, valorCorreo, valorContra, valorApellido, valorCelu;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user=findViewById(R.id.usuario);
        nombre=findViewById(R.id.nom);
        apellido=findViewById(R.id.apellido);
        correo=findViewById(R.id.correo);
        password=findViewById(R.id.contra);
        celular=findViewById(R.id.celular);

        listViewPersonas=findViewById(R.id.muestra);
        inicializarFirebase();
        listarDatos();
    }

    private void listarDatos() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPersonas.clear();
                for (DataSnapshot objSnapchot : snapshot.getChildren()){
                    Persona p= objSnapchot.getValue(Persona.class);
                    listaPersonas.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this,android.R.layout.simple_list_item_1,listaPersonas);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (validaciones()){
            switch (item.getItemId()){
                case R.id.icon_add:{
                    Persona per=new Persona();
                    per.setPid(UUID.randomUUID().toString());
                    per.setApellidos(valorApellido);
                    per.setClave(valorContra);
                    per.setCorreo(valorCorreo);
                    per.setUsuario(valorUser);
                    per.setNombre(valorNom);
                    per.setTelefono(valorCelu);
                    databaseReference.child("Usuario").child(per.getPid()).setValue(per);
                    Toast.makeText(this,"Agregar",Toast.LENGTH_SHORT).show();
                    vaciar();
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
    }

    private boolean validaciones(){
        valorUser= user.getText().toString();
        valorNom=nombre.getText().toString();
        valorCorreo=correo.getText().toString();
        valorContra= password.getText().toString();
        valorApellido= apellido.getText().toString();
        valorCelu=celular.getText().toString();
        if (valorUser.equals("") || valorNom.equals("") || valorCorreo.equals("") ||
                valorContra.equals("")|| valorApellido.equals("")|| valorCelu.equals("")){
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
            return false;
        }

        else
            return true;

    }
}