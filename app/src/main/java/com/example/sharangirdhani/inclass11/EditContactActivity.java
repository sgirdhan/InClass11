package com.example.sharangirdhani.inclass11;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditContactActivity extends AppCompatActivity {

    EditText txtFirstName;
    EditText txtLastName;
    EditText txtPhone;
    EditText txtEmail;
    Button btnUpdate;
    ImageButton icon;
    Uri photoURI;
    ImageButton imagebuttonAvatarUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    Contacts contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        txtFirstName = (EditText) findViewById(R.id.editTextFirstName);
        txtLastName = (EditText) findViewById(R.id.editTextLastName);
        txtPhone = (EditText) findViewById(R.id.editTextPhoneNumber);
        txtEmail = (EditText) findViewById(R.id.editTextEmail);
        icon = (ImageButton) findViewById(R.id.imagebuttonAvatarUser);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        imagebuttonAvatarUser = (ImageButton) findViewById(R.id.imagebuttonAvatarUser);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        photoURI =null;
        String position = getIntent().getExtras().get("position").toString();

        final DatabaseReference userContacts = firebaseDatabase.getReference("contacts").child(position);

        userContacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String firstName = (String) messageSnapshot.child("firstName").getValue();
                    String lastName = (String) messageSnapshot.child("lastName").getValue();
                    String email = (String) messageSnapshot.child("email").getValue();
                    String phoneNumber = (String) messageSnapshot.child("phone").getValue();
                    String image = (String) messageSnapshot.child("image").getValue();
                    contact.setFirstName(firstName);
                    contact.setLastName(lastName);
                    contact.setEmail(email);
                    contact.setPhone(phoneNumber);
                    contact.setImage(image);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        txtFirstName.setText(contact.getFirstName());
        txtLastName.setText(contact.getLastName());
        txtEmail.setText(contact.email);
        txtPhone.setText(contact.phone);
        Picasso.with(this).load(Uri.parse(contact.getImage())).into(imagebuttonAvatarUser);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    contact.setFirstName(txtFirstName.getText().toString());
                    contact.setLastName(txtLastName.getText().toString());
                    contact.setEmail(txtEmail.getText().toString());
                    contact.setPhone(txtPhone.getText().toString());
                    if(photoURI==null){
                        //same do nothing
                    }
                    else{

                        Uri imageUri = photoURI;
                        InputStream inputStream = null;
                        try {
                            inputStream = getContentResolver().openInputStream(imageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (inputStream != null){
                            FirebaseStorage.getInstance().getReference().child(contact.getId()).putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    contact.setImage(taskSnapshot.getDownloadUrl().toString());
                                }
                            });

                        }
                        //upload on the
                    }
                    userContacts.setValue(contact);
                }
                else{
                    Toast.makeText(EditContactActivity.this,"Please check the inputs",Toast.LENGTH_LONG).show();
                }
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                //TODO: do what you have to...
                                photoURI = r.getUri();
                                icon.setImageURI(null);
                                icon.setImageURI(r.getUri());

                            }
                        }).show(EditContactActivity.this);
            }
        });
    }

    boolean validate() {
        if (txtEmail.getText() == null || txtEmail.getText().toString().trim().equals("") ||
                txtFirstName.getText() == null || txtFirstName.getText().toString().trim().equals("") ||
                txtLastName.getText() == null || txtLastName.getText().toString().trim().equals("") ||
                txtPhone.getText() == null || txtPhone.getText().toString().trim().equals("") ||
                txtEmail.getText() == null || txtEmail.getText().toString().trim().equals("")) {
            return false;
        }
        return true;

    }
}