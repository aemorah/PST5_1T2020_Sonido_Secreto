package com.example.smartsound;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartsound.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    EditText user, nombre, apellido, correo, password, celular,contraDispo;
    String valorUser, valorNom, valorCorreo, valorContra, valorApellido, valorCelu, valorPassDispo;
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
        contraDispo=findViewById(R.id.contraDis);
        inicializarFirebase();

    }
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agregar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.icon_exit) {
            regresar();
        } else {
            if (validaciones() && item.getItemId() == R.id.icon_add) {
                validarUsuarioIngreso();
            }

//                    case R.id.icon_save: {
//                        Persona per = new Persona();
//                        //per.setPid(personaSel.getPid());
//                        per.setUsuario(user.getText().toString().trim());
//                        per.setApellidos(apellido.getText().toString().trim());
//                        per.setNombre(nombre.getText().toString().trim());
//                        per.setCorreo(correo.getText().toString().trim());
//                        per.setClave(password.getText().toString().trim());
//                        per.setTelefono(celular.getText().toString().trim());
//                        per.setContrasenaDispositivo(contraDispo.getText().toString());
//                        databaseReference.child(per.getUsuario()).child("Datos").setValue(per);
//                        Toast.makeText(this, "Actualizar", Toast.LENGTH_SHORT).show();
//                        vaciar();
//                        break;
//                    }
        }
        return true;
    }

    private void validarUsuarioIngreso(){
        databaseReference.child("UsersRegis").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Persona per = new Persona();
                per.setApellidos(valorApellido);
                per.setClave(valorContra);
                per.setCorreo(valorCorreo);
                per.setUsuario(valorUser);
                per.setNombre(valorNom);
                per.setTelefono(valorCelu);
                per.setContrasenaDispositivo(valorPassDispo);
                if (!snapshot.hasChild(per.getUsuario())) {
                    databaseReference.child(per.getUsuario()).child("Datos").setValue(per);
                    databaseReference.child("UsersRegis").child(per.getUsuario()).setValue(0);
                    Toast.makeText(MainActivity.this, "Agregado", Toast.LENGTH_SHORT).show();
                    vaciar();
                } else {
                    user.setError("Se usuario ya en uso");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        } else
            return true;


    }


    private void regresar(){
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
    }
}










