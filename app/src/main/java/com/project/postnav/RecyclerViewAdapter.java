package com.project.postnav;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public Context context;
    public ArrayList<Packet> packetList;
    int pos = 0;

    public RecyclerViewAdapter(Context context, ArrayList<Packet> packetList) {
        this.context = context;
        this.packetList = packetList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Packet packet = packetList.get(position);
        String packetType = packet.getType();

        if (packetType.equals("Letter")) {
            holder.type.setImageResource(R.drawable.letter);
        } else if (packetType.equals("Envelope")) {
            holder.type.setImageResource(R.drawable.envelope);
        } else if (packetType.equals("Parcel")) {
            holder.type.setImageResource(R.drawable.parcel);
        }

        holder.primary_key.setText(packet.getPrimaryKey());
        holder.pincode.setText(packet.getPincode());
        holder.weight.setText(packet.getWeight());
        holder.status.setText(packet.getStatus());


    }

    @Override
    public int getItemCount() {
        return packetList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView primary_key;
        public TextView pincode;
        public TextView weight;
        public TextView status;
        public ImageView type;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            int itemCount = getItemCount();
            if(pos < itemCount){
                pos++;
                if(pos % 2 == 0){
                    itemView.findViewById(R.id.list_item).setBackgroundColor(Color.parseColor("#18FFFF"));
                    //itemView.setBackgroundColor(Color.parseColor("#18FFFF"));
                }
                else{
                    itemView.findViewById(R.id.list_item).setBackgroundColor(Color.parseColor("#00B0FF"));
                    //itemView.setBackgroundColor(Color.parseColor("#00B0FF"));
                }
            }



            type = itemView.findViewById(R.id.type);
            primary_key = itemView.findViewById(R.id.primaryKeyFromDB);
            pincode = itemView.findViewById(R.id.pincodeFromDB);
            weight = itemView.findViewById(R.id.weightFromDB);
            status = itemView.findViewById(R.id.statusFromDB);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            Packet packet = packetList.get(position);
            final String primaryKey = packet.getPrimaryKey();
            final String[] state = new String[1];
            final String[] district = new String[1];
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Employees");
                    assert currentUser != null;
                    final DatabaseReference userInfo = reference.child(Objects.requireNonNull(currentUser.getEmail()).replace('.', '_'));

                    userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                state[0] = snapshot.child("State").getValue(String.class);
                                district[0] = snapshot.child("District").getValue(String.class);
                                Intent intent = new Intent(context, PackageActivity.class);
                                intent.putExtra("PrimaryKey", primaryKey);
                                intent.putExtra("State", state[0]);
                                intent.putExtra("District", district[0]);

                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

           // district = "Mumbai";//Need to be commented when merged
            //state = "Maharashtra";//Need to be commented when merged


        }
    }
}