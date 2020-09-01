package com.example.smartsound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Html;
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
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//metodo de la actividad que me donde se valida el ingreso por voz
public class DesbloquearPuerta extends AppCompatActivity {
    //inicializacion de variables
    private List<String> listaDispo= new ArrayList<>();
    private  List<String> listaStatus= new ArrayList<>();
    private List<Integer> listaImg = new ArrayList<>();
    String[] arrayDis;
    String[] arraySta;
    Integer[] arrayimg;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> matches;
    String sEmail;
    String sPassword;
    int contador = 1;
    //Parametros del reconocimiento de voz
    TextView tv;
    String dispoSel;
    private ImageView image;
    private TextView text;
    private static final int RECOGNIZER_RESULT =1;
    ListView listViewDispo;

    //metodo onCreate que donde se inicializan los componentes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_guest);
        tv=findViewById(R.id.tvTitle);
        listViewDispo=findViewById(R.id.listDispo);
        //Se da los valores para credenciales del que envia
        sEmail="smartsound.prueba@gmail.com";
        sPassword="Xsq12345";
        //se inicializa la base de datos
        inicializarFirebase();
        databaseReference.child(GuardadoUsuario.usuarioUsando).child("Datos").addListenerForSingleValueEvent(new ValueEventListener() {
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
        System.out.println(listaDispo);
        dispoSel="";
        //se obtiene el valor del elemento seleccionado en el listview
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
                    Toast.makeText(DesbloquearPuerta.this, "Seleccione Un Dispositivo", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //metodo que se inicializa cuando se aplasta el boton para hablar, lo que activa el api
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //se comienza el query para la base de datos
            databaseReference.child(GuardadoUsuario.usuarioUsando).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    text.setText(matches.get(0).toLowerCase());
                    Persona p = snapshot.child("Datos").getValue(Persona.class);
                    assert p != null;
                    String palabraClave = p.getContrasenaDispositivo().trim();
                    String correoRec = p.getCorreo();
                    System.out.println(correoRec);
                    //si la palabra coincide entonces se cambia el valor
                    if (palabraClave.equalsIgnoreCase(matches.get(0).trim())) {
                        snapshot.child("Dispositivos").child(dispoSel).child("Activacion").getRef().setValue("on");
                    } else {
                        Toast.makeText(DesbloquearPuerta.this, "Ingreso Incorrecto", Toast.LENGTH_SHORT).show();

                        //si se equiva mas de 3 veces entonces se envia un correo
                        if (contador> 3) {

                            //Inicializan las propiedades para mandar el mensaje
                            Properties proporties = new Properties();
                            proporties.put("mail.smtp.auth", "true");
                            proporties.put("mail.smtp.starttls.enable", "true");
                            proporties.put("mail.smtp.host", "smtp.gmail.com");
                            proporties.put("mail.smtp.port", "587");

                            //inicio de sesion
                            Session session = Session.getInstance(proporties, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(sEmail, sPassword);
                                }
                            });

                            try {
                                //inicio del contenido del mensaje
                                Message message = new MimeMessage(session);
                                //correo del que manda
                                message.setFrom(new InternetAddress(sEmail));
                                //correo del que recibe
                                message.setRecipients(Message.RecipientType.TO,
                                        InternetAddress.parse(correoRec));
                                //email tema
                                message.setSubject("Aviso de Seguridad, SmartSound");
                                //email message
                                message.setText("Aviso: Se ha intentado ingresar a la puerta de " + dispoSel + " varias veces.\n Por favor, revisar.");
                                //mandar correo
                                new SendMail().execute(message);

                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                        contador++;

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    //clase para enviar el correo
    private class SendMail extends AsyncTask<Message,String,String> {
        //inicio dle proceso de dialogo
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(DesbloquearPuerta.this, "Numero de intentos exedidos","Informando....",true);

        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Sucess";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "error";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s.equals("Sucess")){
                //Success
                AlertDialog.Builder builder = new AlertDialog.Builder(DesbloquearPuerta.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#509324'>Success</font>"));
                builder.setMessage("Email mandado con exito");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }else{
                //Fail
                Toast.makeText(getApplicationContext(),"Algo salio mal",Toast.LENGTH_SHORT).show();


            }
        }
    }

    //clase myAdapter para poderle dar formato al listview
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

    //metodo donde se obtiene la informacion y se la agrega al listview
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
                    listaImg.add(R.drawable.ic_lock);

                    arrayDis=new String[listaDispo.size()];
                    arrayDis = listaDispo.toArray(arrayDis);
                    arraySta=new String[listaStatus.size()];
                    arraySta = listaStatus.toArray(arraySta);
                    arrayimg=new Integer[listaImg.size()];
                    arrayimg = listaImg.toArray(arrayimg);
                    MyAdapter adapter =new MyAdapter(DesbloquearPuerta.this,arrayDis,arraySta,arrayimg);
                    listViewDispo.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //metodo para inicializar la base de datos
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    //metodo para cambiar el menu de la actividad
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //metodo para asignar acciones a los botones del menu
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

    //metodo para apagar el dispositivo si se aplasta el boton
    public void cerrar(View view) {
        if (!dispoSel.equals("")) {
            databaseReference.child(GuardadoUsuario.usuarioUsando).child("Dispositivos").child(dispoSel).child("Activacion").setValue("off");
        }else
            Toast.makeText(DesbloquearPuerta.this, "Seleccione un Dispositivo", Toast.LENGTH_SHORT).show();
    }

}