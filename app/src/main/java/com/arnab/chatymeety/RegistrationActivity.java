package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.application.isradeleon.notify.Notify;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private Button mbutton;
    private TextInputEditText mName,mEmail,mPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressBar=findViewById(R.id.reg_spin_kit);
        final Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);

        mtoolbar=findViewById(R.id.reg_toolbar);
        mAuth=FirebaseAuth.getInstance();
        mRef=FirebaseDatabase.getInstance().getReference().child("user");
        mbutton=findViewById(R.id.reg_button);
        mName=findViewById(R.id.reg_name);
        mEmail=findViewById(R.id.reg_email);
        mPassword=findViewById(R.id.reg_pass);


        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name=mName.getText().toString();
                String email=mEmail.getText().toString();
                String password=mPassword.getText().toString();
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegistrationActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6){
                    Toast.makeText(RegistrationActivity.this, "Choose a strong password", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                HashMap<String,String> info=new HashMap<>();
                                info.put("name",name);
                                info.put("status","Hi I'm "+name);
                                info.put("imageLink","default");
                                info.put("thumbnail","default");
                                /*https://firebasestorage.googleapis.com/v0/b/chatymeety.appspot.com/o/user%2Fdefault.jpg?alt=media&token=2dfd97cd-745b-4114-940f-0678aa8c11e9*/

                                String uid=mAuth.getCurrentUser().getUid();
                                mRef.child(uid).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegistrationActivity.this, "registration successful", Toast.LENGTH_SHORT).show();

                                        Notify.build(RegistrationActivity.this).setId(1)
                                        .setTitle("ciao! "+name)
                                        .setContent("Click here to make some friends")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setColor(R.color.colorAccent)
                                        //.setLargeIcon("https://images.pexels.com/photos/139829/pexels-photo-139829.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=440")
                                        //.largeCircularIcon()
                                        //.setPicture("https://images.pexels.com/photos/1058683/pexels-photo-1058683.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940")
                                        .setAction(new Intent(getApplicationContext(),AllUserActivity.class))
                                        .show(); // Show notification

                                        startActivity(new Intent(RegistrationActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    }
                                });

                            }
                            else{
                                Toast.makeText(RegistrationActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
}
