package com.example.sharangirdhani.inclass11;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignupActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    ImageButton img;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle(getString(R.string.signupActivityTitle));

        img = (ImageButton) findViewById(R.id.imgBtn_camImage);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ((Button) findViewById(R.id.buttonSignupCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        photoURI = null;
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                photoURI = r.getUri();
                                img.setImageURI(null);
                                img.setImageURI(r.getUri());
                            }
                        }).show(SignupActivity.this);
            }
        });
    }

    public void onClickSignup(View v){
        final String firstname = ((EditText) findViewById(R.id.editSignupFirstname)).getText().toString();
        final String lastname = ((EditText) findViewById(R.id.editSignupLastname)).getText().toString();
        final String email = ((EditText) findViewById(R.id.editSignupEmail)).getText().toString();
        final String password = ((EditText) findViewById(R.id.editSignupPassword)).getText().toString();
        final String confirmPassword = ((EditText) findViewById(R.id.editSignupRepeat)).getText().toString();
        boolean isValid = true;

        if(photoURI == null) {
            Toast.makeText(SignupActivity.this, "Select image", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (firstname.length() == 0) {
            ((EditText) findViewById(R.id.editSignupFirstname)).setError("Please provide your first name");
            isValid = false;
        }
        if (lastname.length() == 0) {
            ((EditText) findViewById(R.id.editSignupLastname)).setError("Please provide your last name");
            isValid = false;
        }
        if (email.length() == 0) {
            ((EditText) findViewById(R.id.editSignupEmail)).setError("Please provide your E-mail/Username");
            isValid = false;
        }
        if (password.length()==0){
            ((EditText) findViewById(R.id.editSignupPassword)).setError("Please provide a password");
            isValid = false;
        }
        else if(!password.equals(confirmPassword)) {
            ((EditText) findViewById(R.id.editSignupRepeat)).setError("Passwords do not match");
            isValid = false;
        }

        if (isValid) {
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final DatabaseReference userId = firebaseDatabase.getReference("users").push();

                        InputStream inp = null;
                        try {
                            inp = getContentResolver().openInputStream(photoURI);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        if(inp != null) {
                            FirebaseStorage.getInstance().getReference().child(userId.getKey()).putStream(inp).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    User user = new User(firstname, lastname, email, password, downloadUrl.toString(), userId.getKey());
                                    userId.setValue(user);
                                }
                            });
                        }

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
