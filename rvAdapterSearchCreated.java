package com.example.stefano.spinup20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Stefano on 10/04/18.
 */

public class rvAdapterSearchCreated extends RecyclerView.Adapter<rvAdapterSearchCreated.ViewHolder>{
    private ArrayList<rvCreatedApp> mData;
    private Context context;
    DatabaseReference databaseReference;

    private String name;
    private String category;
    //private ItemClickListener mClickListener;

    // data is passed into the constructor
    rvAdapterSearchCreated(Context context, ArrayList<rvCreatedApp> data) {
        this.context = context;
        this.mData = data;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        name = "";
        category = "";
    }

    public rvAdapterSearchCreated.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_created, null);
        return new rvAdapterSearchCreated.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final rvAdapterSearchCreated.ViewHolder holder, int position) {
        rvCreatedApp c = mData.get(position);
        name = c.getAppName();
        holder.appName.setText("Name : " + name);

        databaseReference.child("apps").child(c.getAppName()).child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                category = dataSnapshot.getValue(String.class);
                holder.appCategory.setText("Category : " + category);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //String c = mData.get(position);
        //holder.comment.setText(c);
        //holder.creator.setText(c);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView appName;
        TextView appCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.appName);
            appCategory = itemView.findViewById(R.id.appCategory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String prova = mData.get(position).getAppName();
            Intent intent = new Intent(context, AppHome.class);
            Bundle bundle = new Bundle();
            bundle.putString("appName", prova);
            intent.putExtras(bundle);
            context.startActivity(intent);
            //if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /*

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
