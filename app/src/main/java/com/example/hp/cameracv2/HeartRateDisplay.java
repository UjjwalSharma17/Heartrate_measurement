package com.example.hp.cameracv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.hp.cameracv2.FdActivity.ave;
import static com.example.hp.cameracv2.RegisterActivity.userId;

public class HeartRateDisplay extends AppCompatActivity {

    TextView finalHeartRate;
    Button saveButton;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_display);

        finalHeartRate = findViewById(R.id.final_heart_rate);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.heart_rate_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("heartRateInfo");

        finalHeartRate.setText(Integer.toString((int)ave));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                //TODO SAVE HEART RATE INFO
                saveHeartRateInfo();

                startActivity(new Intent(HeartRateDisplay.this,HeartRateHistory.class));

            }
        });


    }

    public void saveHeartRateInfo(){


        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = simpleDateFormat.format(calendar.getTime());

        HeartRateHistoryInfoActivity heartRateInfoObject = new HeartRateHistoryInfoActivity((int)ave,currentDate,currentTime);

        String currentUser = firebaseAuth.getCurrentUser().getUid();
        
        databaseReference.child(currentUser).child(currentTime).setValue(heartRateInfoObject);
        Toast.makeText(this, "Measurement stored successfully in database", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onStop() {
        super.onStop();
        progressBar.setVisibility(View.INVISIBLE);
    }
}
