package com.example.agribot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.agribot.R.string.versionData;

public class DeviceInfoActivity extends AppCompatActivity {

    private TextView versionData;
    private TextView chipsetData;
    private TextView idData;
    private TextView passwordData;
    private TextView ownerData;
    private TextView imeiData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        setupComp();

        loadInfo();
    }

    public void loadInfo() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(LoginActivity.loginID);
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LoginActivity.User user = snapshot.getValue(LoginActivity.User.class);
                assert user != null;
                versionData.setText(R.string.versionData);
                chipsetData.setText(user.getChipset());
                idData.setText(LoginActivity.loginID);
                passwordData.setText(user.getPassword());
                ownerData.setText(user.getOwner());
                imeiData.setText(user.getImei());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setupComp() {
        findViewById(R.id.textViewVer);
        findViewById(R.id.textViewChip);
        findViewById(R.id.textViewID);
        findViewById(R.id.textViewPasswrd);
        findViewById(R.id.textViewOwn);
        findViewById(R.id.textViewIM);
        Button close = (Button) findViewById(R.id.closeInfo);

        versionData = findViewById(R.id.textViewVersionData);
        chipsetData = findViewById(R.id.textViewChipData);
        idData = findViewById(R.id.textViewIDData);
        passwordData = findViewById(R.id.textViewPasswwordData);
        ownerData = findViewById(R.id.textViewOwnerData);
        imeiData = findViewById(R.id.textViewImiData);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
