package com.project.postnav;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    DatabaseReference mref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //  navView.getMenu().removeItem(R.id.ma);
        mref = MainActivity.y;
        MainActivity.first_time = 0;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_scan, R.id.navigation_dashboard)
                .build();

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Employees")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.','_'));
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Status").getValue(String.class).equals("Employee")){
                    navView.getMenu().removeItem(R.id.navigation_dashboard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(MainActivity.admin_status == 1) {
            getMenuInflater().inflate(R.menu.settings, menu);
            return true;
        }
        else
        {
            getMenuInflater().inflate(R.menu.empsettings , menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.manage_emp){

            Intent intent = new Intent(getApplicationContext() , manage_employees.class);
            startActivity(intent);

        }
        if(id == R.id.signout)
        {

            FirebaseAuth.getInstance().signOut();
            //mref.child(MainActivity.Email2);
            SharedPreferences pref = getSharedPreferences("SharedPref",MODE_PRIVATE);
            pref.edit().clear().commit();
            MainActivity.first_time = 1;
            MainActivity.mGoogleSignInClient.signOut();
           // MainActivity act = new MainActivity();

            this.finish();
            finishAffinity();
            Intent intent = new Intent(getApplicationContext() , MainActivity.class);
            startActivity(intent);
            //act.onCreate(null);
            //return true;




        }
        if(id == R.id.profile)
        {
            String status=null;
            String email_address = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                   // AuthenticationActivity.email_address;
            String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if(MainActivity.admin_status == 1)
            {
                status = "Administrator";
            }
            else
            {
                status = "Employee";
            }
            Intent intent = new Intent(getApplicationContext() , Display_profile_activity.class);
            intent.putExtra("email_address",  email_address);
            intent.putExtra("Name" , name);
            intent.putExtra("Status" , status);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Post Navigator")
                .setMessage("\nAre you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}