#include <IOXhop_FirebaseStream.h>
#include <IOXhop_FirebaseESP32.h>


#include <WiFi.h>


#define FIREBASE_HOST "smartsound-2055a.firebaseio.com"   
#define FIREBASE_AUTH "s4zlUBx1e7WMdTmJobvOsTt3WafQqhGDOMOVQUrH"   
#define WIFI_SSID "NETLIFE-BAMBOO"               
#define WIFI_PASSWORD "REYBAMBOO"



int ledAbierto = 16;

int ledEspera = 17;

int ledCerrado = 5;

int ledControl = 18;

int sensorLuz = A0;

int relay = 26;

String idDevice = "Principal";

String estadoPuerta = "estadoPuerta";

String  ruta = "definir ruta";

String activacion = "";

String junta = "";

boolean cerradura;

void setup() {
  Serial.begin(9600);
  pinMode(16, OUTPUT);
  pinMode(17, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(18, OUTPUT);
  pinMode(26, OUTPUT);
  pinMode(A0, INPUT);
                  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(200);
  }
  Serial.println();

  Serial.print("Connected to ");

  Serial.println(WIFI_SSID);

  Serial.print("IP Address is : ");

  Serial.println(WiFi.localIP());    


  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);                                       // connect to firebase

  digitalWrite(ledControl, HIGH);
  delay(1000);

  Serial.println(analogRead(sensorLuz));
  
  if (analogRead(sensorLuz) < 1500){
    cerradura = 1;
    Serial.println("Entro aqui y no hizo verga");
  }else{
    cerradura = 0;
    digitalWrite(relay, LOW);
    digitalWrite(ledEspera,HIGH);
    delay(1000);
    digitalWrite(ledEspera,LOW);
    delay(500);
    digitalWrite(ledEspera,HIGH);
    delay(1000);
    digitalWrite(ledEspera,LOW);
    delay(500);
    digitalWrite(ledEspera,HIGH);
    delay(1000);
    digitalWrite(ledEspera,LOW);
    delay(1000);
    digitalWrite(relay, HIGH);
    Serial.println("Entro y debio titilar");
    digitalWrite(ledCerrado, HIGH);
  }
  
}

 void loop(){
    
    activacion = Firebase.getString("/aleemora/Dispositivos/Principal/Activacion");
    junta = Firebase.getString("/aleemora/Dispositivos/Principal/Estado");
    
    if(analogRead(sensorLuz)<1500 && junta == "no"){
        Firebase.setString("/aleemora/Dispositivos/Principal/Estado","si"); //Aqui junta la huevada
        digitalWrite(ledAbierto,LOW);
        digitalWrite(ledCerrado,HIGH);
    }else if(analogRead(sensorLuz) >= 1500 && junta == "si"){
        Firebase.setString("/aleemora/Dispositivos/Principal/Estado","no"); //aqui dice que no esta junta
        digitalWrite(ledAbierto,HIGH);
        digitalWrite(ledCerrado,LOW);    
    }

    if(activacion == "on" && junta == "no" ){
      digitalWrite(relay, LOW); 
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
