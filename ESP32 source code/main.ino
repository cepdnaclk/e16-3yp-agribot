
// Select your modem:
#define TINY_GSM_MODEM_SIM800 // Modem is SIM800L

// Set serial for debug console (to the Serial Monitor, default speed 115200)
#define SerialMon Serial
// Set serial for AT commands
#define SerialAT Serial1

// Define the serial console for debug prints, if needed
#define TINY_GSM_DEBUG SerialMon

// set GSM PIN, if any
#define GSM_PIN ""

// Your GPRS credentials, if any
const char apn[] = "mobitel"; // APN 
const char gprsUser[] = "";
const char gprsPass[] = "";

// SIM card PIN (leave empty, if not defined)
const char simPIN[]   = ""; 

// MQTT details
const char* broker = "34.229.189.170";                    // Public IP address or domain name
const char* mqttUsername = "agribot1";  // MQTT username
const char* mqttPassword = "abc123";  // MQTT password

// subscribe topics
const char* topicOutput1 = "23xyz53j8/Data";
const char* topicOutput2 = "esp/output2";

// publish topics
const char* topicState = "23xyz53j8/State";
const char* topicTemperature = "23xyz53j8/Sensor/Temperature";
const char* topicHumidity = "23xyz53j8/Sensor/Humidity";

// Define the serial console for debug prints, if needed
//#define DUMP_AT_COMMANDS

#include <Wire.h>
#include <TinyGsmClient.h>

#ifdef DUMP_AT_COMMANDS
  #include <StreamDebugger.h>
  StreamDebugger debugger(SerialAT, SerialMon);
  TinyGsm modem(debugger);
#else
  TinyGsm modem(SerialAT);
#endif

#include <PubSubClient.h>
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include "DHT.h"

// define mpu sensor
Adafruit_MPU6050 mpu;

TinyGsmClient client(modem);
PubSubClient mqtt(client);

// TTGO T-Call pins
#define MODEM_RST            5
#define MODEM_PWKEY          4
#define MODEM_POWER_ON       23
#define MODEM_TX             27
#define MODEM_RX             26
#define I2C_SDA              21
#define I2C_SCL              22

// DHT11 pins
#define DHTPIN               18  // Digital pin connected to the DHT sensor

#define OUTPUT_1             2
#define OUTPUT_2             15

uint32_t lastReconnectAttempt = 0;

// I2C for SIM800 (to keep it running when powered from battery)
TwoWire I2CPower = TwoWire(0);

//TwoWire I2CBME = TwoWire(1);

#define IP5306_ADDR          0x75
#define IP5306_REG_SYS_CTL0  0x00

#define DHTTYPE DHT11   // DHT 11;

#define INPUT_SIZE 30


// Initialize DHT sensor
DHT dht(DHTPIN, DHTTYPE);

float temperature = 0;
float humidity = 0;
long lastMsg = 0;
bool isGot = false;
boolean start = false;

int plantMap [10][10]; 


bool setPowerBoostKeepOn(int en){
  I2CPower.beginTransmission(IP5306_ADDR);
  I2CPower.write(IP5306_REG_SYS_CTL0);
  if (en) {
    I2CPower.write(0x37); // Set bit1: 1 enable 0 disable boost keep on
  } else {
    I2CPower.write(0x35); // 0x37 is default reg value
  }
  return I2CPower.endTransmission() == 0;
}

void mqttCallback(char* topic, byte* message, unsigned int len) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  int plantData[5];
  
  for (int i = 0; i < len; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();

  // Changes the output state according to the message
  if (String(topic) == "23xyz53j8/Data") {
    Serial.println("Changing output to: ");
    if(messageTemp == "start"){
     // if(isGot){
        Serial.println("start signal");
        start = true;
        digitalWrite(OUTPUT_1, HIGH);
        digitalWrite(OUTPUT_2, HIGH);
     // }
    }
    else if(messageTemp == "stop"){
      Serial.println("stop signal");                                                                                       
      digitalWrite(OUTPUT_1, LOW);
      digitalWrite(OUTPUT_2, LOW);

    }
    else if(messageTemp == "pause"){
      Serial.println("pause signal"); 
      start = false;                                                                                       
      digitalWrite(OUTPUT_2, LOW);

    }else{
      // Get next command from Serial (add 1 for final 0)
      char input[len+1];
      messageTemp.toCharArray(input, len);
      // Add the final 0 to end the C string
      input[len+1] = 0;
      isGot = true;
      // Read each command pair 
      char* command = strtok(input, ",");
      int id;
      int inputData;
      
      while (command != 0)
      {
          // Split the command in two values
          char* separator = strchr(command, ':');
          if (separator != 0)
          {
              // Actually split the string in 2: replace ':' with 0
              *separator = 0;
              id = atoi(command);
              ++separator;
              inputData = atoi(separator);
          }
          plantData[id] = inputData;
          // Find the next command in input string
          command = strtok(0, ",");
      }
      Serial.print("row length: ");
      Serial.println(plantData[0]);
      Serial.print("Distance between 2 seeds: ");
      Serial.println(plantData[1]);
      Serial.print("Number of rows: ");
      Serial.println(plantData[2]);
      Serial.print("Distance between row: ");
      Serial.println(plantData[3]);
 
    }
  }
  else if (String(topic) == "esp/output2") {
    Serial.print("Changing output to ");
    if(messageTemp == "true"){
      Serial.println("true");
      digitalWrite(OUTPUT_2, HIGH);
    }
    else if(messageTemp == "false"){
      Serial.println("false");
      digitalWrite(OUTPUT_2, LOW);
    }
  }
}

boolean mqttConnect() {
  SerialMon.print("Connecting to ");
  SerialMon.print(broker);

  // Connect to MQTT Broker without username and password
  boolean status = mqtt.connect("GsmClientN");

  // Or, if you want to authenticate MQTT:
  //boolean status = mqtt.connect("GsmClientN", mqttUsername, mqttPassword);

  if (status == false) {
    SerialMon.println(" Failed to connect broker");
    ESP.restart();
    return false;
  }
  SerialMon.println(" Successfully connected to broker");
  mqtt.subscribe(topicOutput1);
  mqtt.subscribe(topicOutput2);

  return mqtt.connected();
}

void mpu_setup(){
  Serial.println("Adafruit MPU6050 test!");

  // Try to initialize!
  mpu.begin();
  Serial.println("MPU6050 Found!");

  mpu.setAccelerometerRange(MPU6050_RANGE_8_G);
  Serial.print("Accelerometer range set to: ");
  switch (mpu.getAccelerometerRange()) {
  case MPU6050_RANGE_2_G:
    Serial.println("+-2G");
    break;
  case MPU6050_RANGE_4_G:
    Serial.println("+-4G");
    break;
  case MPU6050_RANGE_8_G:
    Serial.println("+-8G");
    break;
  case MPU6050_RANGE_16_G:
    Serial.println("+-16G");
    break;
  }
  mpu.setGyroRange(MPU6050_RANGE_500_DEG);
  Serial.print("Gyro range set to: ");
  switch (mpu.getGyroRange()) {
    case MPU6050_RANGE_250_DEG:
      Serial.println("+- 250 deg/s");
      break;
    case MPU6050_RANGE_500_DEG:
      Serial.println("+- 500 deg/s");
      break;
    case MPU6050_RANGE_1000_DEG:
      Serial.println("+- 1000 deg/s");
      break;
    case MPU6050_RANGE_2000_DEG:
      Serial.println("+- 2000 deg/s");
      break;
  }

  mpu.setFilterBandwidth(MPU6050_BAND_5_HZ);
  Serial.print("Filter bandwidth set to: ");
  switch (mpu.getFilterBandwidth()) {
    case MPU6050_BAND_260_HZ:
      Serial.println("260 Hz");
      break;
    case MPU6050_BAND_184_HZ:
      Serial.println("184 Hz");
      break;
    case MPU6050_BAND_94_HZ:
      Serial.println("94 Hz");
      break;
    case MPU6050_BAND_44_HZ:
      Serial.println("44 Hz");
      break;
    case MPU6050_BAND_21_HZ:
      Serial.println("21 Hz");
      break;
    case MPU6050_BAND_10_HZ:
      Serial.println("10 Hz");
      break;
    case MPU6050_BAND_5_HZ:
      Serial.println("5 Hz");
      break;
  }

  Serial.println("");
  delay(100);
}

void setup() {
  // Set console baud rate
  SerialMon.begin(115200);
  delay(10);
  
  // Start I2C communication
  I2CPower.begin(I2C_SDA, I2C_SCL, 400000);
  
  Serial.println(F(" DHT11 sensor started!"));
  dht.begin();

  mpu_setup();
  // Keep power when running from battery
  bool isOk = setPowerBoostKeepOn(1);
  SerialMon.println(String("IP5306 KeepOn ") + (isOk ? "OK" : "FAIL"));

  // Set modem reset, enable, power pins
  pinMode(MODEM_PWKEY, OUTPUT);
  pinMode(MODEM_RST, OUTPUT);
  pinMode(MODEM_POWER_ON, OUTPUT);
  digitalWrite(MODEM_PWKEY, LOW);
  digitalWrite(MODEM_RST, HIGH);
  digitalWrite(MODEM_POWER_ON, HIGH);
  
  pinMode(OUTPUT_1, OUTPUT);
  pinMode(OUTPUT_2, OUTPUT);
  
  SerialMon.println("Wait...");

  // Set GSM module baud rate and UART pins
  SerialAT.begin(115200, SERIAL_8N1, MODEM_RX, MODEM_TX);
  delay(6000);

  // Restart takes quite some time
  // To skip it, call init() instead of restart()
  SerialMon.println("Initializing modem...");
  //modem.restart();
  modem.init();

  String modemInfo = modem.getModemInfo();
  SerialMon.print("Modem Info: ");
  SerialMon.println(modemInfo);

  // Unlock your SIM card with a PIN if needed
  if ( GSM_PIN && modem.getSimStatus() != 3 ) {
    modem.simUnlock(GSM_PIN);
  }

  SerialMon.print("Connecting to APN: ");
  SerialMon.print(apn);
  if (!modem.gprsConnect(apn, gprsUser, gprsPass)) {
    SerialMon.println("GPRS connection failed");
    ESP.restart();
  }
  else {
    SerialMon.println(" OK");
  }
  
  if (modem.isGprsConnected()) {
    SerialMon.println("GPRS connected");
  }

  // MQTT Broker setup
  mqtt.setServer(broker, 1883);
  mqtt.setCallback(mqttCallback);
}

void loop() {
  if (!mqtt.connected()) {
    SerialMon.println("=== MQTT NOT CONNECTED ===");
    mqtt.publish(topicState, "disconnect");
    // Reconnect every 10 seconds
    uint32_t t = millis();
    if (t - lastReconnectAttempt > 10000L) {
      lastReconnectAttempt = t;
      if (mqttConnect()) {
        mqtt.publish(topicState, "connect");
        lastReconnectAttempt = 0;
      }
    }
    delay(100);
    return;
  }

  if(start){
    /* Get new sensor events with the readings */
    sensors_event_t a, g, temp;
    mpu.getEvent(&a, &g, &temp);
  
    /* Print out the values */
    Serial.print("Acceleration X: ");
    Serial.print(a.acceleration.x);
    Serial.print(", Y: ");
    Serial.print(a.acceleration.y);
    Serial.print(", Z: ");
    Serial.print(a.acceleration.z);
    Serial.println(" m/s^2");
  
    Serial.print("Rotation X: ");
    Serial.print(g.gyro.x);
    Serial.print(", Y: ");
    Serial.print(g.gyro.y);
    Serial.print(", Z: ");
    Serial.print(g.gyro.z);
    Serial.println(" rad/s");
  
    Serial.print("Temperature: ");
    Serial.print(temp.temperature);
    Serial.println(" degC");
  
    Serial.println("");
    delay(500);
  }

  long now = millis();
  if (now - lastMsg > 30000) {
    lastMsg = now;
    
    // Temperature in Celsius
    temperature = dht.readTemperature();   
    // Uncomment the next line to set temperature in Fahrenheit 
    // (and comment the previous temperature line)
    //temperature = 1.8 * bme.readTemperature() + 32; // Temperature in Fahrenheit
    
    // Convert the value to a char array
    char tempString[8];
    dtostrf(temperature, 1, 2, tempString);
    Serial.print("Temperature: ");
    Serial.println(tempString);
    mqtt.publish(topicTemperature, tempString);

    humidity = dht.readHumidity();
    
    // Convert the value to a char array
    char humString[8];
    dtostrf(humidity, 1, 2, humString);
    Serial.print("Humidity: ");
    Serial.println(humString);
    mqtt.publish(topicHumidity, humString);
  }

  mqtt.loop();
}
