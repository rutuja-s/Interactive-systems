
#define trigPin1 5
#define echoPin1 3
#define trigPin2 12
#define echoPin2 11
#define trigPin3 7
#define echoPin3 8
#define trigPin4 10
#define echoPin4 9
#define led0 2
#define led1 4
#define led2 6
#define led3 13

int LightSensorPin = A0; // select the input pin for LDR
int LightSensorValue = 0;
short LightSensorState;
short newState;
int psensorValue;
long a, b, c, d;
void readSensors();

void trigSensors(int trigPin){
  digitalWrite(trigPin, LOW);
  delayMicroseconds(5);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

}



void setup() {

Serial.begin (9600);
pinMode(trigPin1, OUTPUT);
pinMode(echoPin1, INPUT);
pinMode(trigPin2, OUTPUT);
pinMode(echoPin2, INPUT);
pinMode(trigPin3, OUTPUT);
pinMode(echoPin3, INPUT);
pinMode(trigPin4, OUTPUT);
pinMode(echoPin4, INPUT);
pinMode(led0, OUTPUT);
pinMode(led1, OUTPUT);
pinMode(led2, OUTPUT);
pinMode(led3, OUTPUT);
LightSensorValue=analogRead(LightSensorPin);
if(LightSensorValue<600){
  Serial.println("l,0");
  LightSensorState = 0;
}else{
  Serial.println("l,1");
  LightSensorState = 1;
}
psensorValue = analogRead(A1);
  // print out the value you read:
  Serial.println("p,"+(String)psensorValue);
digitalWrite(led0, HIGH);
digitalWrite(led1, HIGH);
digitalWrite(led2, HIGH);
digitalWrite(led3, HIGH);
a=0;b=0;c=0;d=0;

}

void loop() {
// put your main code here, to run repeatedly:

readSensors();
LightSensorValue=analogRead(LightSensorPin);
if(LightSensorValue<600){
  newState = 0;
}else{
  newState = 1;
}
if(newState != LightSensorState){
  Serial.println("l,"+(String)(newState));
  LightSensorState = newState;
}
int psensorNewValue=analogRead(A1);
if(psensorNewValue != psensorValue){
  psensorValue = psensorNewValue;
  Serial.println("p,"+(String)psensorValue);
  
}
if(Serial.available() >= 2){
String serialread = Serial.readString();

switch(serialread[2]){
  case '1': 
  digitalWrite(led1, LOW);
  digitalWrite(led2, LOW);
  digitalWrite(led3, LOW);
  delay(10000);
  digitalWrite(led1, HIGH);
  digitalWrite(led2, HIGH);
  digitalWrite(led3, HIGH);
  break;
  case '2': 
  digitalWrite(led0, LOW);
  digitalWrite(led2, LOW);
  digitalWrite(led3, LOW);
  delay(10000);
  digitalWrite(led0, HIGH);
  digitalWrite(led2, HIGH);
  digitalWrite(led3, HIGH);
  break;
  case '3': 
  digitalWrite(led1, LOW);
  digitalWrite(led0, LOW);
  digitalWrite(led3, LOW);
  delay(10000);
  digitalWrite(led1, HIGH);
  digitalWrite(led0, HIGH);
  digitalWrite(led3, HIGH);
  break;
  case '4': 
  digitalWrite(led1, LOW);
  digitalWrite(led2, LOW);
  digitalWrite(led0, LOW);
  delay(10000);
  digitalWrite(led1, HIGH);
  digitalWrite(led2, HIGH);
  digitalWrite(led0, HIGH);
  break;
}

}



}


void readSensors()
{
  trigSensors(trigPin1);
  a = pulseIn(echoPin1, HIGH)/58.2;
  Serial.println("u0,"+(String)a);
  trigSensors(trigPin2);
  b = pulseIn(echoPin2, HIGH)/58.2;
  Serial.println("u1,"+(String)b);
  trigSensors(trigPin3);
  c = pulseIn(echoPin3, HIGH)/58.2;
  Serial.println("u2,"+(String)c);
  trigSensors(trigPin4);
  d = pulseIn(echoPin4, HIGH)/58.2;
  Serial.println("u3,"+(String)d);

}
