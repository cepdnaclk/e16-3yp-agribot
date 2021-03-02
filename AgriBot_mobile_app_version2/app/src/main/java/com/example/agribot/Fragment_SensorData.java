package com.example.agribot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Fragment_SensorData extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String temp = getArguments().getString("temperature");
        String hum = getArguments().getString("humidity");
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_sensor_data, container, false);
        final TextView tempView = (TextView)root.findViewById(R.id.txtTemperature);
        tempView.setText(temp);
        final TextView humView = (TextView)root.findViewById(R.id.txtHumadity);
        humView.setText(hum);
        return root;
    }
}
