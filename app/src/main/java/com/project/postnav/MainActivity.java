package com.project.postnav;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 2020;
    private FirebaseAuth mAuth;
    private DatabaseReference FireUser;
    public static GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog loadingBar;
    static int first_time = 1;
    int currentU = 0;
     String State=null;
     String District = null;
     String status = null;
    static int admin_status = 0;
    final int time = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ImageView img = findViewById(R.id.imageView);
        Glide.with(this).load(R.raw.loading).into(img);
        FireUser  = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Logging you in...");
        loadingBar.setMessage("Please wait.");
        loadingBar.setCanceledOnTouchOutside(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = this.getSharedPreferences("SharedPref",MODE_PRIVATE);
         State = preferences.getString("State",null);
         District= preferences.getString("District",null);
         status = preferences.getString("Status",null);


        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(State == null || District == null || status == null) {
                signIn();
            }
            else
            {
                AuthenticationActivity.State  = State;
                AuthenticationActivity.District = District;

                if(status.equals("Employee"))
                {
                    admin_status = 0;
                }
                else
                {
                    admin_status = 1;
                }

                Intent intent = new Intent(getApplicationContext() , HomeActivity.class);
                startActivity(intent);
            }
        }, time-100);

        ProgressBar p = findViewById(R.id.loadingBar);

        new CountDownTimer(time, 1) {
            public void onTick(long millisUntilFinished) {
                long finishedSeconds = time - millisUntilFinished;
                int total = (int) (((float)finishedSeconds / (float)time) * 100.0);
                p.setProgress(total);

            }
            public void onFinish() {
                p.setVisibility(View.INVISIBLE);
                findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);
            }
        }.start();

    }
    @Override
    public void onStart() {
        super.onStart();
        /*FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null)
            {
                currentU = 1;
            }
        updateUI(currentUser);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                loadingBar.show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingBar.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private int signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

        return 1;
    }
    static DatabaseReference y;
    int flag=0;
    private void updateUI(final FirebaseUser user) {

        if (user != null)
        {

             y =  AddUserToDatabase(user);
            loadingBar.dismiss();

                            //if(currentU!=1) {
            //SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",MODE_PRIVATE);
            final String[] employee_status = {null};
            final int[] exists = {0};
  /*          DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Employees");
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(Email2).exists())
                    {
                        exists[0] = 1;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if((State == null || District == null)&&exists)
            {
                Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                flag =1;
                startActivity(intent);
                return;
            }*/

            //DatabaseReference ref = FireUser.child("Employees").child(State).child(District).child(Email2);
            DatabaseReference ref = FireUser.child("Employees");
            //.child(user.getEmail().replaceAll(".","_")
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child(Email2).exists()==false)
                    {
                        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                        flag  =1;
                        startActivity(intent);

                    }


                    else
                    {
                        employee_status[0] = snapshot.child(Email2).child("Status").getValue().toString();
                        AuthenticationActivity.District = snapshot.child(Email2).child("District").getValue().toString();
                        AuthenticationActivity.State = snapshot.child(Email2).child("State").getValue().toString();
                        if(employee_status[0].equals( "Employee"))
                        {
                            admin_status = 0;

                        }
                        else
                        {
                            admin_status = 1;

                        }
                        Intent intent = new Intent(getApplicationContext() , HomeActivity.class);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }//#01579B
    //x = 0 means that first time sign up
    static int x = 0;
    static String EmailID;
    static String Email2 = null;
    private DatabaseReference AddUserToDatabase(FirebaseUser user) {

        String EmailId=user.getEmail();

        assert EmailId != null;
        EmailId = EmailId.replace(".","_");
        AuthenticationActivity.email_address = EmailId;
        AuthenticationActivity.Name = user.getDisplayName();

         Email2 = EmailId;

        DatabaseReference ref = FireUser.child("Employees");

        final String finalEmailId = EmailId;
        /*ValueEventListener val = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(Email2).exists())
                {
                    x=1;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };*/

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((!snapshot.child(Email2).exists())&&(flag!=1))
                {
                    Intent intent = new Intent(getApplicationContext() , AuthenticationActivity.class);

                    startActivity(intent);
                }
                else
                {
                    AuthenticationActivity.State = snapshot.child(Email2).child("State").getValue().toString();
                    AuthenticationActivity.District = snapshot.child(Email2).child("District").getValue().toString();
                    Intent intent = new Intent(getApplicationContext() , HomeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


            //ref = ref.child(Email2);
            //DatabaseReference users = FireUser.child("Employees").child(EmailId);
            //DatabaseReference name;
         //   name = users.child("Name");
           // name.setValue(user.getDisplayName());



        return ref;

    }

    @Override public void onBackPressed() {
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