#include <IOXhop_FirebaseStream.h>
#include <IOXhop_FirebaseESP32.h>


#include <WiFi.h>


#define FIREBASE_HOST "smartsound-2055a.firebaseio.com"   
#define FIREBASE_AUTH "s4zlUBx1e7WMdTmJobvOsTt3WafQqhGDOMOVQUrH"   
#define WIFI_SSID "NETLIFE-BAMBOO"               
#define WIFI_PASSWORD "XXXXXXXXXX"



int ledAbierto = 16;

int ledEspera = 17;

int ledCerrado = 5;

int ledControl = 18; //Este es el led que va junto al LDR para verificar que la puerta esté junta.

int sensorLuz = A0; //Se configura este puerto para hacer la lectura del valor del LDR

int relay = 26;

String activacion = "";   //valor para abrir por parte del usuario

String junta = "";  //valor para verificar que la puerta esté junta

boolean cerradura;

void setup() {
  Serial.begin(9600);
  pinMode(16, OUTPUT);
  pinMode(17, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(18, OUTPUT);
  pinMode(26, OUTPUT);
  pinMode(A0, INPUT);
                  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);        //Se configura la red WiFi con los datos de la red Wifi del Hogar                          
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {      //Verifica que haya conexión para poder continuar
    Serial.print(".");
    delay(200);
  }
  Serial.println();

  Serial.print("Connected to ");

  Serial.println(WIFI_SSID);

  Serial.print("IP Address is : ");

  Serial.println(WiFi.localIP());             //Muestra la IP Address asignada


  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);   // Se realiza la conexión con la base de datos enviando los datos del Host y la Key

  digitalWrite(ledControl, HIGH);                 // Se enciende el led de control
  delay(1000);

  Serial.println(analogRead(sensorLuz));          //Lectura de verificación
  
  if (analogRead(sensorLuz) < 1500){              //Todo el bloque if else se enfoca en hacer que las condiciones iniciales de la 
                                                  //puerta se cumplan para poder hacer su correcto funcionamiento
    cerradura = 1;
  }else{
    cerradura = 0;
    digitalWrite(relay, LOW);
    digitalWrite(ledEspera,HIGH);
    delay(1000);
    digitalWrite(ledEspera,LOW);
    delay(500);
    digitalWrite(ledEspera,HIGH);
    delay(1000);
    digitalWrite(ledEspera,LOW);                  //El led titila 3 veces si la puerta no está junta y mete el pestillo para
    delay(500);                                   //poder juntar la puerta y una vez junta, se cierra
    digitalWrite(ledEspera,HIGH);
    delay(1000);
    digitalWrite(ledEspera,LOW);
    delay(1000);
    digitalWrite(relay, HIGH);
    digitalWrite(ledCerrado, HIGH);
  }
  
}

 void loop(){
    
    activacion = Firebase.getString("/aleemora/Dispositivos/Principal/Activacion");   //Se lee el valor de activación en la base de datos con la ruta
    junta = Firebase.getString("/aleemora/Dispositivos/Principal/Estado");            //indicada, también para leer el valor de estado
    
    if(analogRead(sensorLuz)<1500 && junta == "no"){
        Firebase.setString("/aleemora/Dispositivos/Principal/Estado","si"); //Primero se revisa que la puerta no este junta en firebase, si no, entonces si la puerta
        digitalWrite(ledAbierto,LOW);                                       //muestra un valor menor a 1500 en el LDR entonces significa que está junta y se configura
        digitalWrite(ledCerrado,HIGH);                                      // ese valor en firebase
    }else if(analogRead(sensorLuz) >= 1500 && junta == "si"){
        Firebase.setString("/aleemora/Dispositivos/Principal/Estado","no"); //Aqui verifica con la lectura analógica que no esté junta y si la lectura indica que 
        digitalWrite(ledAbierto,HIGH);                                      // no está junta, entonces se configura este valor en la base de datos.
        digitalWrite(ledCerrado,LOW);    
    }

    if(activacion == "on" && junta == "no" ){                               //aqui se lee la activación de la puerta y se abre o se cierra según las lecturas
      digitalWrite(relay, LOW);                                             //estén en on o off.
    }else if(activacion == "on" && junta == "si" ){
      digitalWrite(relay, LOW);
    }else if(activacion == "off"){
      if(junta == "no"){
        digitalWrite(relay, LOW);
      }else{
        digitalWrite(relay, HIGH);
      }

    }

    Serial.println(analogRead(sensorLuz));
  
 }
