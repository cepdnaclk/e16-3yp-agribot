package com.example.agribot;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment_Device extends Fragment {

    private TextView versionData;
    private TextView chipsetData;
    private TextView idData;
    private TextView passwordData;
    private TextView ownerData;
    private TextView imeiData;

    public Fragment_Device() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment__device, container, false);

        setupComp(root);
        loadInfo();

        return root;
    }

    private void loadInfo() {
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

    private void setupComp(ViewGroup root) {
        root.findViewById(R.id.textViewVer);
        root.findViewById(R.id.textViewChip);
        root.findViewById(R.id.textViewID);
        root.findViewById(R.id.textViewPasswrd);
        root.findViewById(R.id.textViewOwn);
        root.findViewById(R.id.textViewIM);

        versionData = root.findViewById(R.id.textViewVersionData);
        chipsetData = root.findViewById(R.id.textViewChipData);
        idData = root.findViewById(R.id.textViewIDData);
        passwordData = root.findViewById(R.id.textViewPasswwordData);
        ownerData = root.findViewById(R.id.textViewOwnerData);
        imeiData = root.findViewById(R.id.textViewImiData);


    }
}
