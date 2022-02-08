package com.aa.chittooassignment.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.aa.chittooassignment.QsnActivity;
import com.aa.chittooassignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentProfile extends Fragment {

    public FragmentProfile() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        Button win = view.findViewById(R.id.winbutton);
        win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), QsnActivity.class));
            }
        });

        TextView tv = view.findViewById(R.id.hellotxt);

        if(!FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(""))
            tv.setText("Hello, "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("AllUser").child(FirebaseAuth.getInstance().getUid());
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv.setText("Hello, "+snapshot.child("Name").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }
}