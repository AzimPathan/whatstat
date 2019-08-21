package com.azimpathan.whatsstat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by AZIM_2 on 19/12/2017.
 */

public class LoginActivity extends AppCompatActivity {

    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static String UID;
    private EditText emailET,passwordET;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Snackbar snackbar=Snackbar.make(findViewById(R.id.loginLayout),"This app is developed by Azim Pathan",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        //Initialize things early
        firebaseAuth=FirebaseAuth.getInstance();
        emailET=findViewById(R.id.etEmail);
        passwordET=findViewById(R.id.etPassword);
        Button loginBtn = findViewById(R.id.btnLogin);
        Button joinBtn=findViewById(R.id.btnJoin);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    UID=firebaseAuth.getCurrentUser().getUid();
                    if(UID!=null)
                        startActivity(new Intent(LoginActivity.this, TabbedActivity.class));
                }
            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailET.getText().toString();
                String password=passwordET.getText().toString();
                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Logging in");
                    progressDialog.setMessage("Please wait..");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                if(firebaseAuth.getCurrentUser()!=null)
                                    UID=firebaseAuth.getCurrentUser().getUid();
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, TabbedActivity.class));
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Invalid Email or Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        //Toast.makeText(LoginActivity.this, "LoginActivity:onResume() called..", Toast.LENGTH_SHORT).show();
        emailET.setText("");
        passwordET.setText("");
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        System.exit(0);
    }

    @Override
    protected void onStart() {
        //Toast.makeText(LoginActivity.this, "LoginActivity:onStart() called..", Toast.LENGTH_SHORT).show();
        super.onStart();
        //firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}