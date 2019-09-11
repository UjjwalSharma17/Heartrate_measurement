package com.example.hp.cameracv2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HeartRateHistory extends AppCompatActivity {

    ListView listView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    List<HeartRateHistoryInfoActivity> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_history);
        firebaseAuth = FirebaseAuth.getInstance();

        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("heartRateInfo").child(currentUserId);


        listView = findViewById(R.id.list_view);
        historyList = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                historyList.clear();
                for(DataSnapshot historyInfoSnapshot : dataSnapshot.getChildren()){

                    String heartRate = historyInfoSnapshot.child("heartRate").getValue().toString();
                    String date = historyInfoSnapshot.child("mDate").getValue().toString();
                    String time = historyInfoSnapshot.child("mTime").getValue().toString();
                    HeartRateHistoryInfoActivity historyInfo = new HeartRateHistoryInfoActivity(Integer.parseInt(heartRate),date,time);
                    historyList.add(historyInfo);

                }

                HeartRateHistoryList adapter = new HeartRateHistoryList(HeartRateHistory.this, historyList);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
