package com.example.sharangirdhani.inclass11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class CreateContactActivity extends AppCompatActivity {
    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phoneNumber;

    Button btnCreate;
    Button btnReset;
    ImageButton imageButton;
    Uri photoURI;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        firstName = (TextView) findViewById(R.id.editTextFirstNameUser);
        lastName = (TextView) findViewById(R.id.editTextLastNameUser);
        email = (TextView) findViewById(R.id.editTextEmail);
        phoneNumber = (TextView) findViewById(R.id.editTextUserName);

        btnCreate = (Button) findViewById(R.id.btnAdd);
        btnReset = (Button) findViewById(R.id.btnReset);
        imageButton = (ImageButton) findViewById(R.id.imagebuttonAvatarUser);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        photoURI = null;
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                photoURI = r.getUri();
                                imageButton.setImageURI(null);
                                imageButton.setImageURI(r.getUri());
                            }
                        }).show(CreateContactActivity.this);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName.setText("");
                lastName.setText("");
                email.setText("");
                phoneNumber.setText("");
                imageButton.setImageResource(R.drawable.default_cam);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String first = firstName.getText().toString();
                final String last = lastName.getText().toString();
                final String em = email.getText().toString();
                final String phone = phoneNumber.getText().toString();
                boolean isValid = true;

                if (first.length() == 0) {
                    firstName.setError("Please provide your first name");
                    isValid = false;
                }
                if (last.length() == 0) {
                    lastName.setError("Please provide your last name");
                    isValid = false;
                }
                if (em.length() == 0) {
                    email.setError("Please provide your E-mail");
                    isValid = false;
                }

                if (phone.length() == 0) {
                    phoneNumber.setError("Please provide your Phone Number");
                    isValid = false;
                }

                if(photoURI == null) {
                    Toast.makeText(CreateContactActivity.this, "Select image", Toast.LENGTH_LONG).show();
                    isValid = false;
                }

                if(isValid) {
                    final DatabaseReference contactId = firebaseDatabase.getReference("contacts").child(firebaseAuth.getCurrentUser().getUid()).push();

                    InputStream inp = null;
                    try {
                        inp = getContentResolver().openInputStream(photoURI);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if(inp != null) {
                        FirebaseStorage.getInstance().getReference().child(contactId.getKey()).putStream(inp).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Contacts contacts = new Contacts();
                                contacts.setEmail(em);
                                contacts.setId(contactId.getKey());
                                contacts.setFirstName(first);
                                contacts.setLastName(last);
                                contacts.setPhone(phone);
                                contacts.setImage(downloadUrl.toString());

                                contactId.setValue(contacts);
                            }
                        });
                    }
                    Toast.makeText(CreateContactActivity.this,"Contact created successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateContactActivity.this, ContactsListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



    }
}
