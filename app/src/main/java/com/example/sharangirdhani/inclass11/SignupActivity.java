package com.example.sharangirdhani.inclass11;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle(getString(R.string.signupActivityTitle));

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ((Button) findViewById(R.id.buttonSignupCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClickSignup(View v){
        final String firstname = ((EditText) findViewById(R.id.editSignupFirstname)).getText().toString();
        final String lastname = ((EditText) findViewById(R.id.editSignupLastname)).getText().toString();
        final String email = ((EditText) findViewById(R.id.editSignupEmail)).getText().toString();
        final String password = ((EditText) findViewById(R.id.editSignupPassword)).getText().toString();

        boolean isValid = true;

        if (firstname.length() == 0) {
            ((EditText) findViewById(R.id.editSignupFirstname)).setError("Please provide your first name");
            isValid = false;
        }
        if (lastname.length() == 0) {
            ((EditText) findViewById(R.id.editSignupLastname)).setError("Please provide your last name");
            isValid = false;
        }
        if (email.length() == 0) {
            ((EditText) findViewById(R.id.editSignupEmail)).setError("Please provide your E-mail");
            isValid = false;
        }
        if (password.length()==0){
            ((EditText) findViewById(R.id.editSignupPassword)).setError("Please provide a password");
            isValid = false;
        }

        if (isValid) {
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this,"User created successfully",Toast.LENGTH_SHORT).show();


                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(firstname + " " + lastname)
                                .build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(changeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseAuth.getInstance().signOut();
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password);
                                finish();
                            }
                        });

                    } else {
                        try{
                            throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e) {
                            Toast.makeText(SignupActivity.this,"Password too weak",Toast.LENGTH_SHORT).show();
                        }catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(SignupActivity.this,"Username already exists",Toast.LENGTH_SHORT).show();
                        }catch (FirebaseAuthInvalidUserException e) {
                            Toast.makeText(SignupActivity.this,"Username not valid",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
