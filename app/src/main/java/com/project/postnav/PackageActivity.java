package com.project.postnav;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PackageActivity extends AppCompatActivity implements View.OnClickListener{
    TextView from,to,weight,pinCode,status,batchCode,currLoc;
    ImageView type;
    String cnt,UDist,UState;
    FloatingActionButton editInfo,updateStatus,updateLocation;
    TimeStampAdapter timeStampAdapter;
    DatabaseReference TSRef,ref = FirebaseDatabase.getInstance().getReference();
    String PrimaryKey,State,District;
    RecyclerView ts_recyclerView;
    //private ProgressDialog loadingBar;

    ArrayList<TimeStamp> TSList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);
        Initialize();
        Bundle b = getIntent().getExtras();
        if(b!=null){
            PrimaryKey = b.get("PrimaryKey").toString();
            State = b.get("State").toString();
            District = b.get("District").toString();
            ref = ref.child("Packages").child(State).child(District).child(PrimaryKey);
        }

        ts_recyclerView.setLayoutManager(new LinearLayoutManager(PackageActivity.this));
        TSList = new ArrayList<>();
        Objects.requireNonNull(getSupportActionBar()).setTitle(PrimaryKey);
        TSRef = ref.child("Timestamp");
        TSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TSList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    TimeStamp ts = dataSnapshot1.getValue(TimeStamp.class);
                    TSList.add(ts);
                }
                timeStampAdapter = new TimeStampAdapter(PackageActivity.this,TSList);
                ts_recyclerView.setAdapter(timeStampAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void Initialize() {

        /*loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Post Navigator");
        loadingBar.setMessage("Fetching data... Please wait!!");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();*/

        type = findViewById(R.id.type);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        weight = findViewById(R.id.weight);
        pinCode = findViewById(R.id.pinCode);
        status = findViewById(R.id.status);
        batchCode = findViewById(R.id.batchCode);
        currLoc = findViewById(R.id.currLoc);
        ts_recyclerView = findViewById(R.id.timestamp_recyclerView);

        editInfo = findViewById(R.id.fabEditInfo);
        editInfo.setOnClickListener(this);
        updateStatus = findViewById(R.id.fabUpdateStatus);
        updateStatus.setOnClickListener(this);
        updateLocation = findViewById(R.id.fabUpdateLocation);
        updateLocation.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                from.setText(dataSnapshot.child("From").getValue(String.class));
                to.setText(dataSnapshot.child("To").getValue(String.class));
                status.setText(dataSnapshot.child("Status").getValue(String.class));
                currLoc.setText(dataSnapshot.child("CurrentLocation").getValue(String.class));
                pinCode.setText(dataSnapshot.child("Pincode").getValue(String.class));
                weight.setText(dataSnapshot.child("Weight").getValue(String.class));
                batchCode.setText(dataSnapshot.child("Batch").getValue(String.class));
                switch (Objects.requireNonNull(dataSnapshot.child("Type").getValue(String.class))){
                    case "Parcel":
                        type.setImageResource(R.drawable.parcel);
                        break;
                    case "Letter":
                        type.setImageResource(R.drawable.letter);
                        break;
                    case "Envelope":
                        type.setImageResource(R.drawable.envelope);
                        break;
                    default:
                        type.setImageResource(R.drawable.postlogo2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("Timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cnt = String.valueOf(dataSnapshot.getChildrenCount()+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Employees")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.','_'));
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UDist = snapshot.child("District").getValue(String.class);
                UState = snapshot.child("State").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.fabEditInfo){
            Intent i = new Intent(PackageActivity.this,update_info.class);
            i.putExtra("Primary Key", PrimaryKey);
            i.putExtra("State", State);
            i.putExtra("District", District);
            startActivity(i);
        }
        if(v.getId()==R.id.fabUpdateStatus){
            String[] StatusArray = getResources().getStringArray(R.array.Status);
            final Spinner sp = new Spinner(PackageActivity.this);
            final ArrayAdapter<String> adp = new ArrayAdapter<>(PackageActivity.this,
                    android.R.layout.simple_list_item_1, StatusArray);

            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sp.setAdapter(adp);

            AlertDialog.Builder builder = new AlertDialog.Builder(PackageActivity.this);
            builder.setTitle(PrimaryKey);
            builder.setView(sp);
            builder.setPositiveButton("Okay", (dialogInterface, i) -> {

                String status = sp.getSelectedItem().toString();
                if(status.equals(StatusArray[0])){
                    Toast.makeText(PackageActivity.this,"Status not selected!",Toast.LENGTH_SHORT).show();
                }
                else{
                    updateStatusInDB(status);
                    Toast.makeText(PackageActivity.this,"Status Updated!",Toast.LENGTH_SHORT).show();
                }

            });
            builder.setNegativeButton("Cancel",((dialogInterface, i) -> { }));
            builder.setCancelable(true);
            builder.create().show();
        }
        if(v.getId()==R.id.fabUpdateLocation){
            AlertDialog.Builder builder = new AlertDialog.Builder(PackageActivity.this);
            builder.setTitle(PrimaryKey);
            final EditText inputLoc = new EditText(this);
            inputLoc.setInputType(InputType.TYPE_CLASS_TEXT);
            inputLoc.setPadding(40,25,40,25);
            inputLoc.setHint("Enter new Location");
            builder.setView(inputLoc);
            builder.setPositiveButton("Okay", (dialogInterface, i) -> {
                String location = inputLoc.getText().toString().trim();
                if(location.length()>0){
                    updateLocationInDB(location);
                    Toast.makeText(PackageActivity.this,"Location Updated!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PackageActivity.this,"Invalid Location",Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel",((dialogInterface, i) -> { }));
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    void updateStatusInDB(String str){
        DatabaseReference reference = ref.child("Timestamp");
        ref.child("Status").setValue(str);
        if(cnt.length()==1)
            cnt = "00"+cnt;
        if(cnt.length()==2)
            cnt = "0"+cnt;
        reference = reference.child("TS"+cnt);
        reference.child("Time").setValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        reference.child("Date").setValue(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        reference.child("Location").setValue(UDist+", "+UState);
        reference.child("Remark").setValue("Status Updated to \""+str+"\".");
    }

    void updateLocationInDB(String str){
        DatabaseReference reference = ref.child("Timestamp");
        ref.child("CurrentLocation").setValue(str);
        if(cnt.length()==1)
            cnt = "00"+cnt;
        if(cnt.length()==2)
            cnt = "0"+cnt;
        reference = reference.child("TS"+cnt);
        reference.child("Time").setValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        reference.child("Date").setValue(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        reference.child("Location").setValue(UDist+", "+UState);
        reference.child("Remark").setValue("Current Location Updated to \""+str+"\".");
    }
}