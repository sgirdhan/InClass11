package com.example.sharangirdhani.inclass11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

public class EditUserActivity extends AppCompatActivity {

    ImageButton image;
    EditText firstName;
    EditText lastName;
    Button btnUpdate;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        firstName = (EditText) findViewById(R.id.editTextFirstNameUser);
        lastName = (EditText) findViewById(R.id.editTextLastNameUser);
        image = (ImageButton) findViewById(R.id.imagebuttonAvatarUser);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        photoURI = null;
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                photoURI = r.getUri();
                                image.setImageURI(null);
                                image.setImageURI(r.getUri());
                            }
                        }).show(EditUserActivity.this);
            }
        });

        final DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                firstName.setText(currentUser.getFirstName());
                lastName.setText(currentUser.getLastName());
                Picasso.with(EditUserActivity.this).load(currentUser.getImg()).into(image);
                photoURI = Uri.parse(currentUser.getImg());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                final String first = firstName.getText().toString();
                final String last = lastName.getText().toString();
                if(photoURI == null) {
                    Toast.makeText(EditUserActivity.this, "Select image", Toast.LENGTH_LONG).show();
                    isValid = false;
                }

                if (first.length() == 0) {
                    firstName.setError("Please provide your first name");
                    isValid = false;
                }
                if (last.length() == 0) {
                    lastName.setError("Please provide your last name");
                    isValid = false;
                }

                if(isValid) {
                    userRef.child("firstName").setValue(first);
                    userRef.child("lastName").setValue(last);
                    userRef.child("img").setValue(photoURI.toString());

                    UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(first + " " + last)
                            .build();
                    firebaseAuth.getCurrentUser().updateProfile(changeRequest);

                    Intent intent = new Intent(EditUserActivity.this, ContactsListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
