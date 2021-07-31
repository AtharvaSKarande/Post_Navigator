package com.project.postnav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticationActivity extends AppCompatActivity {

    private final DatabaseReference FireUser = FirebaseDatabase.getInstance().getReference();
    public static String State;
    public static String District;

    static String email_address;
    static String Name;
    String[] items2;
    ArrayAdapter<String> adapter2;

   FirebaseUser muser;
    FirebaseAuth mauth;
    FirebaseDatabase mdata;
    static SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


        sharedpreferences = this.getSharedPreferences("SharedPref",Context.MODE_PRIVATE);
        final Spinner dropdown2 = findViewById(R.id.spinner2);
        final Spinner dropdown3 = findViewById(R.id.spinner3);
        dropdown3.setVerticalScrollBarEnabled(true);
        dropdown2.setVerticalScrollBarEnabled(true);
        final Spinner dropdown = findViewById(R.id.spinner1);
        dropdown.setVerticalScrollBarEnabled(true);
        String [] items3 = new String[]{"- Select Position -","Employee","Administrator"};
        String[] items = getResources().getStringArray(R.array.State_dropdown);
        items[0] = "- Select State -";
        mauth = FirebaseAuth.getInstance();
        muser= mauth.getCurrentUser();
       mdata= FireUser.getDatabase();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,items3);
         items2= new String[]{"-Select District-"};
        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);
        dropdown3.setAdapter(adapter3);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 1)
                    items2 = getResources().getStringArray(R.array.State_Andhra_Pradesh);
                if(position == 2)
                    items2 = getResources().getStringArray(R.array.State_Arunachal_Pradesh);
                if(position == 3)
                    items2 = getResources().getStringArray(R.array.State_Assam);
                if(position == 4)
                    items2 = getResources().getStringArray(R.array.State_Bihar);
                if(position == 5)
                    items2 = getResources().getStringArray(R.array.State_Chhattisgarh);
                if(position == 6)
                    items2 = getResources().getStringArray(R.array.State_Goa);
                if(position == 7)
                    items2 = getResources().getStringArray(R.array.State_Gujarat);
                if(position == 8)
                    items2 = getResources().getStringArray(R.array.State_Haryana);
                if(position == 9)
                    items2 = getResources().getStringArray(R.array.State_Himachal_Pradesh);
                if(position == 10)
                    items2 = getResources().getStringArray(R.array.State_Jharkhand);
                if(position == 11)
                    items2 = getResources().getStringArray(R.array.State_Karnataka);
                if(position == 12)
                    items2 = getResources().getStringArray(R.array.State_Kerala);
                if(position == 13)
                    items2 = getResources().getStringArray(R.array.State_Madhya_Pradesh);
                if(position == 14)
                    items2 = getResources().getStringArray(R.array.State_Maharashtra);
                if(position == 15)
                    items2 = getResources().getStringArray(R.array.State_Manipur);
                if(position == 16)
                    items2 = getResources().getStringArray(R.array.State_Meghalaya);
                if(position == 17)
                    items2 = getResources().getStringArray(R.array.State_Mizoram);
                if(position == 18)
                    items2 = getResources().getStringArray(R.array.State_Nagaland);
                if(position == 19)
                    items2 = getResources().getStringArray(R.array.State_Odisha);
                if(position == 20)
                    items2 = getResources().getStringArray(R.array.State_Punjab);
                if(position == 21)
                    items2 = getResources().getStringArray(R.array.State_Rajasthan);
                if(position == 22)
                    items2 = getResources().getStringArray(R.array.State_Sikkim);
                if(position == 23)
                    items2 = getResources().getStringArray(R.array.State_Tamil_Nadu);
                if(position == 24)
                    items2 = getResources().getStringArray(R.array.State_Telangana);
                if(position == 25)
                    items2 = getResources().getStringArray(R.array.State_Tripura);
                if(position == 26)
                    items2 = getResources().getStringArray(R.array.State_Uttar_Pradesh);
                if(position == 27)
                    items2 = getResources().getStringArray(R.array.State_Uttarakhand);
                if(position == 28)
                    items2 = getResources().getStringArray(R.array.State_West_Bengal);
                if(position == 29)
                    items2 = getResources().getStringArray(R.array.UT_Andaman_and_Nicobar_Islands);
                if(position == 30)
                    items2 = getResources().getStringArray(R.array.UT_Chandigarh);
                if(position == 31)
                    items2 = getResources().getStringArray(R.array.UT_Dadra_and_Nagar_Haveli);
                if(position == 32)
                    items2 = getResources().getStringArray(R.array.UT_Daman_and_Diu);
                if(position == 33)
                    items2 = getResources().getStringArray(R.array.UT_Delhi);
                if(position == 34)
                    items2 = getResources().getStringArray(R.array.UT_Jammu_and_Kashmir);
                if(position == 35)
                    items2 = getResources().getStringArray(R.array.UT_Ladakh);
                if(position == 36)
                    items2 = getResources().getStringArray(R.array.UT_Lakshadweep);
                if(position == 37)
                    items2 = getResources().getStringArray(R.array.UT_Puducherry);
                items2[0] = "- Select District -";
                adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items2);
                dropdown2.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdown.setAdapter(adapter);



        Button b = findViewById(R.id.button);
        b.setOnClickListener(v -> {
            if(!dropdown.getSelectedItem().toString().equals("- Select State -")){
                if(!dropdown2.getSelectedItem().toString().equals("- Select District -")){
                    if(!dropdown3.getSelectedItem().toString().equals("- Select Position -")) {
                        DatabaseReference childref = mdata.getReference().child("Employees").child(MainActivity.Email2);
                        childref.child("State").setValue(dropdown.getSelectedItem().toString());
                        childref.child("District").setValue(dropdown2.getSelectedItem().toString());
                        childref.child("Name").setValue(muser.getDisplayName());
                        childref.child("Status").setValue(dropdown3.getSelectedItem().toString());
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Status", dropdown3.getSelectedItem().toString());
                        editor.putString("State", dropdown.getSelectedItem().toString());
                        editor.putString("District", dropdown2.getSelectedItem().toString());
                        editor.apply();
                        if (dropdown3.getSelectedItem().toString().equals("Employee")) {
                            MainActivity.admin_status = 0;
                        } else {
                            MainActivity.admin_status = 1;
                        }
                        Name = muser.getDisplayName();
                        email_address = muser.getEmail();
                        State = dropdown.getSelectedItem().toString();
                        District = dropdown2.getSelectedItem().toString();

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(AuthenticationActivity.this,"Select your position to sign in",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(AuthenticationActivity.this,"Select District to sign in",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(AuthenticationActivity.this,"Select State and District to sign in",Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {

    }
}