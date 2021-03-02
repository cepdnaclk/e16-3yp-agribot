package com.example.agribot;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static com.example.agribot.LoginActivity.userTopic;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private static final String TAG = "MyActivity";
    String topic = userTopic;
    private MqttClient client;
    private FirebaseDatabase firebaseDatabase;
    private TextView deviceStat;
    private TextView temp;
    public static String tempDataVar = "";
    public static String humDataVar = "";
    private String publishTopic = topic + "/Data";

    public void publishStartSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("start".getBytes());
            msg.setQos(2);
            if (this.client.isConnected()) {
                this.client.publish(publishTopic, msg);
                Toast.makeText(getApplicationContext(), "Start signal sent", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Connection Lost");
                Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void publishPauseSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("pause".getBytes());
            msg.setQos(2);
            if (this.client.isConnected()) {
                this.client.publish(publishTopic, msg);
                Toast.makeText(getApplicationContext(), "Pause signal sent", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Connection Lost");
                Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void publishStopSignalToBroker(View view) {
        if (this.client.isConnected()) {
            // Build an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            // Set a title for alert dialog
            builder.setTitle("STOP SIGNAL!");
            // Ask the final question
            builder.setMessage("Are you sure to stop the process?");
            // Set the alert dialog yes button click listener
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MqttMessage msg = new MqttMessage("stop".getBytes());
                    msg.setQos(2);
                    try {
                        client.publish(publishTopic, msg);
                        Toast.makeText(getApplicationContext(), "Stop signal sent", Toast.LENGTH_SHORT).show();
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Set the alert dialog no button click listener
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when No button clicked
                    Toast.makeText(getApplicationContext(),
                            "No Button Clicked",Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            // Display the alert dialog on interface
            dialog.show();
        } else {
            Log.d(TAG, "Connection Lost");
            Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void publishResetSignalToBroker(View view) {
        if (this.client.isConnected()) {
            // Build an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            // Set a title for alert dialog
            builder.setTitle("RESET SIGNAL!");
            // Ask the final question
            builder.setMessage("Are you sure to reset data?");
            // Set the alert dialog yes button click listener
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MqttMessage msg = new MqttMessage("reset".getBytes());
                    msg.setQos(2);
                    try {
                        client.publish(publishTopic, msg);
                        Toast.makeText(getApplicationContext(), "Reset signal sent", Toast.LENGTH_SHORT).show();
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Set the alert dialog no button click listener
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when No button clicked
                    Toast.makeText(getApplicationContext(),
                            "No Button Clicked",Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            // Display the alert dialog on interface
            dialog.show();
        } else {
            Log.d(TAG, "Connection Lost");
            Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearFields(EditText t1, EditText t2, EditText t3, EditText t4) {
        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
    }

    public void publishMapDataToBroker(View view) {
        EditText text1 = findViewById(R.id.editText5);
        EditText text2 = findViewById(R.id.editText8);
        EditText text3 = findViewById(R.id.editText6);
        EditText text4 = findViewById(R.id.editText7);

        String rowLength = text1.getText().toString();
        String seedGap = text2.getText().toString();
        String numberOfRows = text3.getText().toString();
        String rowGap = text4.getText().toString();

        if (ValidateMapData.isDataEmpty(rowLength, seedGap, numberOfRows, rowGap)) {
            Toast.makeText(MainActivity.this, "Empty Fields!", Toast.LENGTH_SHORT).show();
            clearFields(text1, text2, text3, text4);
        } else if (ValidateMapData.validate(rowLength, seedGap, numberOfRows, rowGap)) {
            String mapData = "{1:" + rowLength + ", 2:" + seedGap + ", 3:" + numberOfRows + ", 4:" + rowGap + "}";
            try {
                MqttMessage msg = new MqttMessage(mapData.getBytes());
                msg.setQos(2);
                if (this.client.isConnected()) {
                    this.client.publish(publishTopic, msg);
                    Toast.makeText(getApplicationContext(), "Data Sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Connection Lost");
                    Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
            }
            clearFields(text1, text2, text3, text4);
        } else {
            Toast.makeText(MainActivity.this, "Invalid Data!", Toast.LENGTH_SHORT).show();
            clearFields(text1, text2, text3, text4);
        }
    }

    @SuppressLint("WrongConstant")
    private void subscribeToBroker() {
        try {
            MqttConnectOptions extraOps = new MqttConnectOptions();
            extraOps.setConnectionTimeout(2);
            extraOps.setAutomaticReconnect(true);
            extraOps.setKeepAliveInterval(15);
            //extraOps.setUserName("metana username eka dapan");
            //extraOps.setPassword(metana password eka dapan);
            extraOps.setCleanSession(true);

            String subscriberTopic1 = topic + "/State";
            String subscriberTopic2 = topic + "/Sensor/Temperature";
            String subscriberTopic3 = topic + "/Sensor/Humidity";
            String[] subscriberTopics = {subscriberTopic1, subscriberTopic2, subscriberTopic3};

            this.client = new MqttClient("tcp://192.168.1.4:1883", LoginActivity.loginID, new MemoryPersistence());
            Log.d(TAG, LoginActivity.loginID);
            this.client.setCallback((MqttCallback) this);
            this.client.connect(extraOps);
            this.client.subscribe(subscriberTopics);

            Log.d(TAG, "Connected to Server");
            Toast.makeText(getApplicationContext(), "Connected to Server", Toast.LENGTH_SHORT).show();

        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Connection Timeout!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subscribeToBroker();

        setContentView(R.layout.activity_main);

        deviceStat = findViewById(R.id.textViewDeviceStat);
        temp = findViewById(R.id.txtTemperature);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                    new Fragment_Configuration()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_config:
                            selectedFragment = new Fragment_Configuration();
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                                    selectedFragment).commit();
                            break;
                        case R.id.nav_params:
                            selectedFragment = new Fragment_Tune();
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                                    selectedFragment).commit();
                            break;
                        case R.id.nav_sensor_data:
                            Bundle data = new Bundle();
                            data.putString("temperature", tempDataVar);
                            data.putString("humidity", humDataVar);
                            selectedFragment = new Fragment_SensorData();
                            selectedFragment.setArguments(data);
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                                    selectedFragment).commit();
                            break;
                        case R.id.nav_logout:
                            selectedFragment = new Fragment_Device();
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                                    selectedFragment).commit();
                            break;
                    }

                    return true;
                }
            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMenu:
            case R.id.deviceInfo: {
                break;
            }
            case R.id.logoutMenu: {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Logout!");
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.dbTimeout = true;
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),
                                "Logout Cancelled!",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Toast.makeText(MainActivity.this, "Connection Lost!!!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "connectionLost");
    }

    @Override
    public void messageArrived(String inputTopic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        if (inputTopic.equals(topic + "/State") && payload.equals("connect")) {
            deviceStat.setText("Connected");
            deviceStat.setBackgroundResource(R.drawable.ic_connected);
        }
        if (inputTopic.equals(topic + "/State") && payload.equals("disconnect")) {
            deviceStat.setText("Disconnected");
            deviceStat.setBackgroundResource(R.drawable.ic_disconnected);
        }
        if (inputTopic.equals(topic + "/Sensor/Temperature")) {
            //TextView tempData = findViewById(R.id.textView6);
            //tempData.setText(payload);
            tempDataVar = payload;
           // temp.setText(tempDataVar);
            Log.d(TAG, payload);
        }
        if (inputTopic.equals(topic + "/Sensor/Humidity")) {
           // TextView humidData = findViewById(R.id.textView6);
          //  humidData.setText(payload);
            humDataVar = payload;
           // deviceStat.setText(humDataVar);
            Log.d(TAG, payload);
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

        Log.d(TAG, "Message Delivered");
    }
    /*
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("Re-Connection Attempt " + reconnect);
        if(reconnect) {
            try {
                String subscriberTopics = topic + "/#";
                MqttTopic.validate(this.topic, true);
                this.client.subscribe(subscriberTopics);
            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }*/
}
