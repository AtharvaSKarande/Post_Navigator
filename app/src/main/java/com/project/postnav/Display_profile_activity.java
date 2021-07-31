package com.project.postnav;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class Display_profile_activity extends AppCompatActivity {
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile_activity);
        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Employees");
        getSupportActionBar().hide();
         email = getIntent().getStringExtra("email_address").replaceAll("_",".");
        final String name = getIntent().getStringExtra("Name");
        String status = getIntent().getStringExtra("Status");
        TextView textStaus = findViewById(R.id.textView4);
        TextView textViewemail = findViewById(R.id.textView6);
        TextView textViewname = findViewById(R.id.textView2);
        textViewemail.setText(email);

        textViewname.setText(name);
        textStaus.setText(status);

        Button remove_Account_button = findViewById(R.id.button2);
        Button promote_Account_button = findViewById(R.id.button3);
        remove_Account_button.setVisibility(View.INVISIBLE);
        promote_Account_button.setVisibility(View.INVISIBLE);

        remove_Account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.admin_status == 1)
                {
                    //ref.child(email.replaceAll(".","_")).child("Name").removeValue();
                    DatabaseReference ref2 = ref.child(email.replaceAll(".","_"));
                     ref.removeValue();
                    SharedPreferences pref = getSharedPreferences("SharedPref",MODE_PRIVATE);
                    pref.edit().clear().apply();
                    MainActivity.first_time = 1;
                    MainActivity.mGoogleSignInClient.signOut();
                    // MainActivity act = new MainActivity();


                    finishAffinity();
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                    //
                }
                else
                {
                    Toast.makeText(Display_profile_activity.this, "Only for Admins", Toast.LENGTH_SHORT).show();
                }
            }
        });

        promote_Account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.admin_status == 1) {
                    Map<String, Object> map = null;
                    map.put("Status", "Administrator");
                    ref.child(email.replaceAll(".", "_")).updateChildren(map);
                }
                else
                {
                    Toast.makeText(Display_profile_activity.this, "Only for Admins", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(Display_profile_activity.this, "Promoted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}