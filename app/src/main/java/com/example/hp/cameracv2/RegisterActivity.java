package com.example.hp.cameracv2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;


public class RegisterActivity extends AppCompatActivity {

    EditText mEmailEditText;
    EditText mName;
    EditText mAge;
    EditText mPasswordEditText;
    Button mRegister;
    ProgressBar registerProgress;

    String name;
    String email;
    String password;
    int age;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authListener;

    public static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailEditText = findViewById(R.id.register_mail);
        mName = findViewById(R.id.register_name);
        mAge = findViewById(R.id.register_age);
        mPasswordEditText = findViewById(R.id.register_password);
        mRegister = findViewById(R.id.register_button);
        registerProgress = findViewById(R.id.register_progress);

        firebaseAuth = FirebaseAuth.getInstance();

//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                if (firebaseUser != null) {
//                    userId = firebaseUser.getUid();
//                }
//            }
//        };

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerProgress.setVisibility(View.VISIBLE);
                mRegister.setVisibility(View.INVISIBLE);

                registerNewUser();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

        registerProgress.setVisibility(View.INVISIBLE);

    }

    private void registerNewUser(){

        name = mName.getText().toString().trim();
        email = mEmailEditText.getText().toString().trim();
        password = mPasswordEditText.getText().toString().trim();
        age = Integer.parseInt(mAge.getText().toString().trim());

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(mAge.getText().toString().trim()) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();

            registerProgress.setVisibility(View.INVISIBLE);
            mRegister.setVisibility(View.VISIBLE);

            return;
        }



        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //userId = databaseReference.push().getKey();
                    userId = firebaseAuth.getCurrentUser().getUid();
                    User newUser = new User(userId,name,age,email,password);

                    saveUserInfo(newUser);
                    //TODO store user info in firebase

                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "Failed To Register", Toast.LENGTH_SHORT).show();
                }
                registerProgress.setVisibility(View.INVISIBLE);
                mRegister.setVisibility(View.VISIBLE);
                finish();
            }
        });


        //startActivity(new Intent(RegisterActivity.this,AccountActivity.class));

    }

    private void saveUserInfo(User newUser){

        databaseReference.child(userId).setValue(newUser);

    }


}
