package com.project.postnav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class manage_employees extends AppCompatActivity {
    ArrayList<recyclec_class> list;
    private DatabaseReference FireUser = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref;
    FirebaseDatabase database;
    recycler_view_adapter adapter;
    DataSnapshot snaps;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employees);
        list = new ArrayList<recyclec_class>();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        adapter= new recycler_view_adapter(list);
        recyclerView.setAdapter(adapter);
       recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                       @Override public void onItemClick(View view, int position) {
                         TextView textView  = view.findViewById(R.id.contact_name);
                         String name = textView.getText().toString();
                            callIntent(name);
                       }

                   @Override public void onLongItemClick(View view, int position) {
                       // do whatever
                   }
               })
       );
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       //ref  = FirebaseDatabase.getInstance().getReference().child("Employees").child(AuthenticationActivity.State).child(AuthenticationActivity.District);
       ref  = FirebaseDatabase.getInstance().getReference().child("Employees");
        email= FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll(".","_");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
               adapter.notifyDataSetChanged();
                snaps = snapshot;
            Iterable<DataSnapshot> iterable = snapshot.getChildren();


               for(DataSnapshot iter : iterable)
               {
                   String state = iter.child("State").getValue().toString();
                   String city  = iter.child("District").getValue().toString();
                   String key = iter.getKey();

                   if(iter.child("State").getValue().toString().equals(AuthenticationActivity.State) && iter.child("District").getValue().toString().equals(AuthenticationActivity.District) &&(!iter.getKey().equals(email))) {
                       String s = iter.child("Name").getValue().toString();
                       String status = iter.child("Status").getValue().toString();

                       list.add(new recyclec_class(s, status));
                       adapter.notifyDataSetChanged();
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });




    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();

    }

    public void callIntent(String name)
    {
        for(DataSnapshot s : snaps.getChildren())
        {
            if(s.child("Name").getValue().toString() == name)
            {
                String email_address = s.getKey();
                String status = s.child("Status").getValue().toString();
                Intent intent = new Intent(getApplicationContext() , Display_profile_activity.class);
                intent.putExtra("email_address",  email_address);
                intent.putExtra("Name" , name);
                intent.putExtra("Status",status);
                startActivity(intent);
            }
        }
    }

}
