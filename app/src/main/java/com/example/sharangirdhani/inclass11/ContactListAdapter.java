package com.example.sharangirdhani.inclass11;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;



public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactRecyclerViewHolder> implements Handler.Callback{

    private Context mContext;
    private List<Contacts> contactsList;
    private SharedPreferences sharedPreferences;
    private String token;
    private Handler handler;
    private IChatHandler chatHandler;

    public ContactListAdapter(Context mContext, List<Contacts> contactsList, IChatHandler chatHandler) {
        this.mContext = mContext;
        this.contactsList = contactsList;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.token = sharedPreferences.getString("token","");
        this.handler = new android.os.Handler(this);
        this.chatHandler = chatHandler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    public static class ContactRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView name;
        TextView phone;
        ImageButton edit;
        TextView email;
        ImageButton delete;

        public ContactRecyclerViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            name = (TextView) itemView.findViewById(R.id.textViewName);
            phone = (TextView) itemView.findViewById(R.id.textViewPhone);
            email = (TextView) itemView.findViewById(R.id.textViewEmail);
            delete = (ImageButton) itemView.findViewById((R.id.imageButtonDelete));
            edit = (ImageButton) itemView.findViewById(R.id.imageButtonEdit);
        }
    }


    @Override
    public ContactRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.contact_row,parent,false);
        ContactRecyclerViewHolder contactRecyclerViewHolder = new ContactRecyclerViewHolder(view);
        return contactRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactRecyclerViewHolder holder, final int position) {
        final Contacts contact = contactsList.get(position);
        holder.name.setText(contact.getFirstName() + " " + contact.getLastName());
        holder.phone.setText(contact.getPhone());
        holder.email.setText(contact.email);
        String url = contact.getImage();
        Picasso.with(mContext).load(url).into(holder.icon);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                //delete from list
                chatHandler.deleteContact(position);

            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,EditContactActivity.class);
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    interface IChatHandler{
        void deleteContact(int position);
    }
}
