package com.project.postnav;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class update_info extends AppCompatActivity {

    String PrimaryKey, State, District, firstType, firstStatus, BatchCode, from, to, newStatus, newType,cnt,UDist,UState;
    int Weight;
    DatabaseReference reference;
    ImageView typeImage;
    TextView PrimaryKeyTT, BatchCodeTT;
    EditText FromET, ToET, WeightET;
    Spinner typeSpinner, statusSpinner;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        typeImage = findViewById(R.id.type_image_update);
        PrimaryKeyTT = findViewById(R.id.primary_key_update);
        BatchCodeTT = findViewById(R.id.batch_code_update);
        FromET = findViewById(R.id.from_update);
        ToET = findViewById(R.id.to_update);
        typeSpinner = findViewById(R.id.type_spinner_update);
        statusSpinner = findViewById(R.id.status_spinner_update);
        WeightET = findViewById(R.id.weight_update);
        saveButton = findViewById(R.id.save);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            PrimaryKey = bundle.get("Primary Key").toString();
            State = bundle.get("State").toString();
            District = bundle.get("District").toString();

            reference = FirebaseDatabase.getInstance().getReference().child("Packages").child(State).child(District).child(PrimaryKey);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    firstType = snapshot.child("Type").getValue(String.class);
                    firstStatus = snapshot.child("Status").getValue(String.class);
                    BatchCode = snapshot.child("Batch").getValue(String.class);
                    from = snapshot.child("From").getValue(String.class);
                    to = snapshot.child("To").getValue(String.class);
                    Weight = Integer.parseInt(Objects.requireNonNull(snapshot.child("Weight").getValue()).toString());
                    newType = firstType;
                    newStatus = firstStatus;

                    final ArrayAdapter<CharSequence> arrayAdapterType = ArrayAdapter.createFromResource(Objects.requireNonNull(getApplicationContext()), R.array.filterType, android.R.layout.simple_spinner_dropdown_item);
                    arrayAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    typeSpinner.setAdapter(arrayAdapterType);

                    switch (firstType) {
                        case "Letter":
                            typeImage.setImageResource(R.drawable.letter);
                            typeSpinner.setSelection(1);
                            break;
                        case "Envelope":
                            typeImage.setImageResource(R.drawable.envelope);
                            typeSpinner.setSelection(2);
                            break;
                        case "Parcel":
                            typeImage.setImageResource(R.drawable.parcel);
                            typeSpinner.setSelection(3);
                            break;
                    }

                    typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(parent.getItemAtPosition(position).equals("Letter")){
                                newType = "Letter";
                            } else if(parent.getItemAtPosition(position).equals("Envelope")){
                                newType = "Envelope";
                            } else if(parent.getItemAtPosition(position).equals("Parcel")){
                                newType = "Parcel";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    PrimaryKeyTT.setText(PrimaryKey);
                    BatchCodeTT.setText(BatchCode);
                    FromET.setText(from);
                    ToET.setText(to);
                    WeightET.setText(String.valueOf(Weight));

                    final ArrayAdapter<CharSequence> arrayAdapterStatus = ArrayAdapter.createFromResource(Objects.requireNonNull(getApplicationContext()), R.array.filterStatus, android.R.layout.simple_spinner_dropdown_item);
                    arrayAdapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    statusSpinner.setAdapter(arrayAdapterStatus);

                    switch (firstStatus) {
                        case "Shipped":
                            statusSpinner.setSelection(1);
                            break;
                        case "Arrived":
                            statusSpinner.setSelection(2);
                            break;
                        case "Out for delivery":
                            statusSpinner.setSelection(3);
                            break;
                        case "Delivered":
                            statusSpinner.setSelection(4);
                            break;
                    }

                    statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(parent.getItemAtPosition(position).equals("Shipped")){
                                newStatus = "Shipped";
                            } else if(parent.getItemAtPosition(position).equals("Arrived")){
                                newStatus = "Arrived";
                            } else if(parent.getItemAtPosition(position).equals("Out for delivery")){
                                newStatus = "Out for delivery";
                            } else if(parent.getItemAtPosition(position).equals("Delivered")){
                                newStatus = "Delivered";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    saveButton.setOnClickListener(v -> {
                        int flag = 0;
                        if (isChangedET(from, FromET, "From")) {
                            from = FromET.getText().toString();
                            flag = 1;
                            Toast.makeText(getApplicationContext(), "Information Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        if(isChangedET(to, ToET, "To")){
                            to = ToET.getText().toString();
                            flag = 1;
                            Toast.makeText(getApplicationContext(), "Information Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        if(isChangedET(String.valueOf(Weight), WeightET, "Weight")){
                            Weight = Integer.parseInt(WeightET.getText().toString());
                            flag = 1;
                            Toast.makeText(getApplicationContext(), "Information Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        if(isChangedSpinner(firstType, newType, "Type")){
                            firstType = newType;
                            flag = 1;
                            Toast.makeText(getApplicationContext(), "Information Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        if (isChangedSpinner(firstStatus, newStatus, "Status")) {
                            firstStatus = newStatus;
                            flag = 1;
                            Toast.makeText(getApplicationContext(), "Information Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        if(flag == 0) {
                            Toast.makeText(getApplicationContext(), "Data is same and cannot be updated", Toast.LENGTH_LONG).show();
                        }
                        else{
                            addTimestamp();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference ref = reference.child("Timestamp");
        ref.addValueEventListener(new ValueEventListener() {
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

    private void addTimestamp() {
        if(cnt.length()==1)
            cnt = "00"+cnt;
        if(cnt.length()==2)
            cnt = "0"+cnt;
        DatabaseReference ref = reference.child("Timestamp").child("TS"+cnt);
        ref.child("Time").setValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        ref.child("Date").setValue(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        ref.child("Location").setValue(UDist+", "+UState);
        ref.child("Remark").setValue("Information Updated.");
    }

    public boolean isChangedET(String str, EditText editText, String child) {
        String newString = editText.getText().toString();
        if (!newString.equals(str)) {
            reference.child(child).setValue(newString);
            return true;
        }
        return false;
    }

    public boolean isChangedSpinner(String firstString, String newString, String typeOrStatus){
        if(!firstString.equals(newString)){
            if(typeOrStatus.equalsIgnoreCase("Type")){
                if(newString.equalsIgnoreCase("Letter")){
                    typeImage.setImageResource(R.drawable.letter);
                } else if(newString.equalsIgnoreCase("Envelope")){
                    typeImage.setImageResource(R.drawable.envelope);
                } else if(newString.equalsIgnoreCase("Parcel")){
                    typeImage.setImageResource(R.drawable.parcel);
                }
            }
            reference.child(typeOrStatus).setValue(newString);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(update_info.this,PackageActivity.class);
        i.putExtra("PrimaryKey", PrimaryKey);
        i.putExtra("State", State);
        i.putExtra("District", District);
        startActivity(i);
    }
}