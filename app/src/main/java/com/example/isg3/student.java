package com.example.isg3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class student extends AppCompatActivity {
    LinearLayout list ,news,postule;
    Button log;
    FirebaseAuth auth;
    FirebaseUser user;
    private TextView txtbirth , txtFullName , txtEmail,txtDepartment ;
    private ProgressBar progressBar;
    private String fullName , email , datte , departement ;
    private ImageView imageView;
    private FirebaseAuth authProfile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        getSupportActionBar().setTitle("Home");
        auth = FirebaseAuth.getInstance();
        log = findViewById(R.id.logout);
        list =findViewById(R.id.list);
        news = findViewById(R.id.news);
        txtFullName = findViewById(R.id.profilname);
        txtEmail = findViewById(R.id.profemail);
        txtbirth = findViewById(R.id.profiledate);
        txtDepartment = findViewById(R.id.profildepart);
        postule = findViewById(R.id.postule);
        progressBar = findViewById(R.id.progre_bar);


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(student.this, "Something went Wrong, no data for now", Toast.LENGTH_SHORT).show();

        }else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }



        user= auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), adminlist.class);
                startActivity(intent);
                finish();
            }
        });

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        postule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyEvents.class);
                startActivity(intent);
                finish();
            }
        });

        authProfile = FirebaseAuth.getInstance();


        if (firebaseUser == null) {
            Toast.makeText(student.this, "Something went wrong, no data for now", Toast.LENGTH_SHORT).show();
        } else {

            // Retrieve the admin status for the current user
            // Retrieve the admin status for the current user
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Registered users").child(firebaseUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean isAdmin = dataSnapshot.child("isAdmin").getValue(Boolean.class);

                        if (isAdmin != null && isAdmin) {
                            postule.setVisibility(View.GONE);
                        } else {
                            postule.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });

        }



    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    fullName = readUserDetails.fullname;
                    datte = readUserDetails.birth;
                    departement = readUserDetails.departe;
                    email = firebaseUser.getEmail();
                    txtFullName.setText(fullName);
                    txtEmail.setText(email);
                    txtbirth.setText(datte);
                    txtDepartment.setText(departement);


                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(student.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public void onBackPressed() {
        // Start the Student activity
        Intent intent = new Intent(student.this, student.class);
        startActivity(intent);

        // Call the super method to allow the default back button behavior
        super.onBackPressed();
    }


}