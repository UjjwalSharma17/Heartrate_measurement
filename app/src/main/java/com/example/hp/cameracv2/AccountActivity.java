package com.example.hp.cameracv2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static com.example.hp.cameracv2.RegisterActivity.userId;

public class AccountActivity extends AppCompatActivity {

    Button mLogoutButton;
    Button heartRateButton;
    Button history;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    TextView mName;
    TextView mAge;
    TextView mEmail;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mLogoutButton = findViewById(R.id.logout_button);
        mName = findViewById(R.id.user_name);
        mAge = findViewById(R.id.user_age);
        mEmail = findViewById(R.id.user_email);
        heartRateButton = findViewById(R.id.measure_hr);
        history = findViewById(R.id.history);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("mName").getValue().toString());
                mAge.setText(dataSnapshot.child("mAge").getValue().toString());
                mEmail.setText(dataSnapshot.child("mEmail").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        heartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AccountActivity.this,FdActivity.class));

            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(AccountActivity.this,LoginActivity.class));

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AccountActivity.this,HeartRateHistory.class));

            }
        });

    }
}
