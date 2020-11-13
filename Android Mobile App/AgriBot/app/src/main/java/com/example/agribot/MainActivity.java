package com.example.agribot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private static final String TAG = "MyActivity";
    private final String topic = "test1";
    private MqttClient client;

    public void publishStartSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("start".getBytes());
            msg.setQos(2);
            this.client.publish(this.topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishPauseSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("pause".getBytes());
            msg.setQos(2);
            this.client.publish(this.topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishStopSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("stop".getBytes());
            msg.setQos(2);
            this.client.publish(this.topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishResetSignalToBroker(View view) {
        try {
            MqttMessage msg = new MqttMessage("reset".getBytes());
            msg.setQos(2);
            this.client.publish(this.topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMapDataToBroker(View view) {
        EditText text1 = findViewById(R.id.editText1);
        EditText text2 = findViewById(R.id.editText2);
        EditText text3 = findViewById(R.id.editText3);
        EditText text4 = findViewById(R.id.editText4);

        String rowLength = text1.getText().toString();
        String seedGap = text2.getText().toString();
        String numberOfRows = text3.getText().toString();
        String rowGap = text4.getText().toString();

        String mapData = "{1:"+rowLength+", 2:"+seedGap+", 3:"+numberOfRows+", 4:"+rowGap+"}";

        try {
            MqttMessage msg = new MqttMessage(mapData.getBytes());
            msg.setQos(2);
            this.client.publish(this.topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        text1.setText("");
        text2.setText("");
        text3.setText("");
        text4.setText("");
    }

    private void subscribeToBroker() {
        try {
            this.client = new MqttClient("tcp://192.168.1.5:1883", "AndroidThingSub", new MemoryPersistence());
            this.client.setCallback((MqttCallback) this);
            this.client.connect();
            this.client.subscribe(this.topic);
            Log.d(TAG, "connectionLost");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscribeToBroker();

    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        TextView publishedData = findViewById(R.id.textView);
        publishedData.setText(payload);
        Log.d(TAG, payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete");
    }
}
