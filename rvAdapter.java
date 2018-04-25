package com.example.stefano.spinup20;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Stefano on 03/04/18.
 */

public class rvAdapter extends RecyclerView.Adapter<rvAdapter.ViewHolder> {

    private ArrayList<rvCommentClass> mData;
    private Context context;
    //private ItemClickListener mClickListener;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser fbUser;

    private int newValue;
    private String currentValue;

    private GoogleSignInAccount profile;
    private String userID;

    // data is passed into the constructor
    rvAdapter(Context context, ArrayList<rvCommentClass> data) {
        this.context = context;
        this.mData = data;

        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fbUser = firebaseAuth.getCurrentUser();

        newValue = 0;
        currentValue = "";

        profile = GoogleSignIn.getLastSignedInAccount(context);
        userID = "";
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_row, null);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        rvCommentClass c = mData.get(position);

        String replace = c.getComment();
        replace = replace.replace("£", ".");
        replace = replace.replace("%", "$");
        replace = replace.replace("&", "[");
        replace = replace.replace("^", "]");
        replace = replace.replace("ç", "#");
        replace = replace.replace("§", "/");

        String replace_bis = c.getCreator();
        replace_bis = replace_bis.replace("£", ".");
        replace_bis = replace_bis.replace("%", "$");
        replace_bis = replace_bis.replace("&", "[");
        replace_bis = replace_bis.replace("^", "]");
        replace_bis = replace_bis.replace("ç", "#");
        replace_bis = replace_bis.replace("§", "/");

        holder.comment.setText(replace);
        holder.creator.setText(replace_bis);
        holder.like.setText("Likes : " + c.getLikes());
        holder.appName = c.getAppName();
        holder.likeButton.setText(c.getButton());
        checkWhoSignIn();

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentCreator = holder.creator.getText().toString().trim();
                String commentText = holder.comment.getText().toString().trim();

                if(holder.likeButton.getText().equals("Like")) {
                    like(commentCreator, commentText, holder.appName);
                }
                
                else {
                    unlike(commentCreator, commentText, holder.appName);
                }

                //Reload the activity
                Intent intent = new Intent(context, AppHome.class);
                Bundle bundle = new Bundle();
                bundle.putString("appName", holder.appName);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });



        //String c = mData.get(position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView comment;
        TextView creator;
        TextView like;
        Button likeButton;
        String appName;

        public ViewHolder(View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            creator = itemView.findViewById(R.id.creator);
            like = itemView.findViewById(R.id.like);
            likeButton = itemView.findViewById(R.id.likeButton);
            appName = "";
            //itemView.setOnClickListener(this);
        }

    /*
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    */
    }

    // ******************** UTILITY FUNCTIONS **********************************

    private void like(final String commentCreator, final String commentText, final String appName) {
        String replace = commentCreator;
        replace = replace.replace(".", "£");
        replace = replace.replace("$", "%");
        replace = replace.replace("[", "&");
        replace = replace.replace("]", "^");
        replace = replace.replace("#", "ç");
        final String last = replace.replace("/", "§");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentValue = dataSnapshot.child("apps").child(appName).child("comments").child(last).child(commentText).getValue(String.class);
                if(currentValue != null) {
                    newValue = Integer.parseInt(currentValue);
                }
                newValue = newValue + 1;
                dataSnapshot.child("apps").child(appName).getRef().child("comments").child(last).child(commentText).setValue(Integer.toString(newValue));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(userID).child("liked_comments").child(appName).child(commentText).setValue(commentText);

        //Reload the activity
    }

    private void unlike(final String commentCreator, final String commentText, final String appName) {
        String replace = commentCreator;
        replace = replace.replace(".", "£");
        replace = replace.replace("$", "%");
        replace = replace.replace("[", "&");
        replace = replace.replace("]", "^");
        replace = replace.replace("#", "ç");
        final String last = replace.replace("/", "§");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentValue =  dataSnapshot.child("apps").child(appName).child("comments").child(last).child(commentText).getValue(String.class);
                if(currentValue != null) {
                    newValue = Integer.parseInt(currentValue);
                }
                newValue = newValue - 1;
                dataSnapshot.child("apps").child(appName).getRef().child("comments").child(last).child(commentText).setValue(Integer.toString(newValue));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(userID).child("liked_comments").child(appName).child(commentText).removeValue();
    }

    private void checkWhoSignIn() {
        if(profile != null) {
            userID = profile.getId();
        }
        else {
            userID = fbUser.getUid();
        }
    }
}

