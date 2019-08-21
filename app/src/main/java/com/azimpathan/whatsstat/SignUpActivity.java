package com.azimpathan.whatsstat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    //private TextInputLayout nameIL,emailIL,passwordIL;
    private EditText nameET,emailET,passwordET;
    private Button signUpBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth=FirebaseAuth.getInstance();
        /*nameIL=findViewById(R.id.inputLayoutName);
        emailIL=findViewById(R.id.inputLayoutEmail);
        passwordIL=findViewById(R.id.inputLayoutPassword);
        */
        nameET=findViewById(R.id.sign_etName);
        emailET=findViewById(R.id.sign_etEmail);
        passwordET=findViewById(R.id.sign_etPassword);

        signUpBtn=findViewById(R.id.btnSignUp);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });

    }

    void signUpUser()
    {
        final String name=nameET.getText().toString();
        String email=emailET.getText().toString();
        String password=passwordET.getText().toString();
        final ProgressDialog progressDialog=new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("Plese wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    String UserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
                    HashMap<String,String> user=new HashMap<>();
                    user.put("Name",name);
                    user.put("Pic","default");
                    user.put("PicUploadTime", String.valueOf(ServerValue.TIMESTAMP));
                    user.put("Thumb","default");
                    databaseReference.setValue(user);
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"You have signed up successfully..",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"Sorry, something went wrong!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}