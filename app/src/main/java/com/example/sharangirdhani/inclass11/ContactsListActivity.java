package com.example.sharangirdhani.inclass11;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContactsListActivity extends AppCompatActivity implements ContactListAdapter.IChatHandler{
    TextView tvFullName;
    ImageButton btnAddContact;
    ImageButton btnEditProfile;
    RecyclerView rvContacts;
    ContactListAdapter recyclerAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Contacts> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        contactsList = new ArrayList<>();

        tvFullName = (TextView) findViewById(R.id.textViewFullName);
        btnAddContact = (ImageButton) findViewById(R.id.imageButtonAdd);
        btnEditProfile = (ImageButton) findViewById(R.id.imageButtonEditMain);
        rvContacts = (RecyclerView) findViewById(R.id.recyclerViewContacts);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        tvFullName.setText(firebaseAuth.getCurrentUser().getDisplayName());

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.row);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.getCustomView().findViewById(R.id.imgBtnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ContactsListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsListActivity.this, CreateContactActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsListActivity.this, EditUserActivity.class);
                startActivityForResult(intent, 200);
            }
        });

        recyclerAdapter = new ContactListAdapter(this,contactsList,this);

        rvContacts.setAdapter(recyclerAdapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        firebaseDatabase.getReference().child("contacts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                contactsList.add(contacts);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvFullName.setText(firebaseAuth.getCurrentUser().getDisplayName());
    }

    @Override
    public void deleteContact(int position) {
        Contacts contacts = contactsList.get(position);
        firebaseDatabase.getReference().child("contacts").child(contacts.getId()).setValue(null);
        contactsList.remove(position);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200) {
            if(resultCode == RESULT_OK) {
                tvFullName.setText(firebaseAuth.getCurrentUser().getDisplayName());
            }
            else {
                Toast.makeText(ContactsListActivity.this, "Wrong Result Code", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(ContactsListActivity.this, "Wrong Request Code", Toast.LENGTH_LONG).show();
        }
    }
}
