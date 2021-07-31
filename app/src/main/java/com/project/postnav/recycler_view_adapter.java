package com.project.postnav;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class recycler_view_adapter extends RecyclerView.Adapter<recycler_view_adapter.ViewHolder>
{
    private List<recyclec_class> mContacts;

    // Pass in the contact array into the constructor
    public recycler_view_adapter(List<recyclec_class> list) {

        this.mContacts = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public ImageView imageView;


        public ViewHolder(View itemView) {

            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            imageView = (ImageView)itemView.findViewById(R.id.imageView2);


        }


    }
    @Override
    public recycler_view_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recycler_view_layout_file, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(recycler_view_adapter.ViewHolder holder, int position) {
        // Get the data model based on position
        recyclec_class contact = mContacts.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(contact.getName());
        ImageView imageView = holder.imageView;

        imageView.setImageResource(R.drawable.ic_baseline_face_24);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }
}
