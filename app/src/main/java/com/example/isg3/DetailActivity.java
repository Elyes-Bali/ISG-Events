package com.example.isg3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    TextView detailDesc , detailTitle;
    ImageView detailImage;
    FloatingActionButton deleteButton,allusers;
    FloatingActionMenu floatingActionMenu;
    String key = "";
    String imageUrl = "";
    Button postuledByButton;
    Dialog dialog;
    RecyclerView recyclerView;
    private FirebaseAuth authProfile;
    private UserAdapter userAdapter; // Declare the adapter variable
    private List<ReadWriteUserDetails> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        allusers= findViewById(R.id.dialogue);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        getSupportActionBar().setTitle("Events");
        dialog = new Dialog(DetailActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.setCancelable(false);
        recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(userAdapter); // Set the adapter for the recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button cancel = dialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, "cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        allusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayUsersInDialog();
                dialog.show();
            }
        });

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        floatingActionMenu = findViewById(R.id.floatingActionMenu);
        postuledByButton = findViewById(R.id.postuledByButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");

            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(DetailActivity.this, "Something went wrong, no data for now", Toast.LENGTH_SHORT).show();
        } else {

            // Retrieve the admin status for the current user
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Registered users").child(firebaseUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean isAdmin = dataSnapshot.child("isAdmin").getValue(Boolean.class);

                        if (isAdmin != null && isAdmin) {
                            floatingActionMenu.setVisibility(View.VISIBLE);
                        } else {
                            floatingActionMenu.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }



        postuledByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference tutorialRef = FirebaseDatabase.getInstance().getReference().child("Android Tutorials").child(key);
                    tutorialRef.child("postuledBy").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                List<String> postuledByList = new ArrayList<>();
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String user = userSnapshot.getValue(String.class);
                                    postuledByList.add(user);
                                }
                                postuledByList.add(userId);
                                tutorialRef.child("postuledBy").setValue(postuledByList)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(DetailActivity.this, "Joined Successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(DetailActivity.this, "Failed Join", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                List<String> postuledByList = new ArrayList<>();
                                postuledByList.add(userId);
                                tutorialRef.child("postuledBy").setValue(postuledByList)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(DetailActivity.this, "Joined Successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(DetailActivity.this, "Failed Join", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error
                        }
                    });
                }
            }
        });






        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Android Tutorials");

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();

                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), adminlist.class));
                        finish();
                    }
                });
            }
        });
    }


    private void displayUsersInDialog() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Registered users");

        DatabaseReference postuledByRef = FirebaseDatabase.getInstance().getReference()
                .child("Android Tutorials").child(key).child("postuledBy");

        postuledByRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> postuledByList = new ArrayList<>();

                // Retrieve the user IDs from postuledBy field
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getValue(String.class);
                    postuledByList.add(userId);
                }

                // Fetch user data based on the user IDs
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<ReadWriteUserDetails> userList = new ArrayList<>();

                        // Iterate over the users in the Registered users node
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.getKey();

                            // Check if the user ID is present in postuledByList
                            if (postuledByList.contains(userId)) {
                                // Fetch the user data
                                String fullName = userSnapshot.child("fullname").getValue(String.class);
                                String departe = userSnapshot.child("departe").getValue(String.class);
                                String emailing = userSnapshot.child("emailing").getValue(String.class);
                                String birth = userSnapshot.child("birth").getValue(String.class);

                                ReadWriteUserDetails user = new ReadWriteUserDetails(fullName, emailing, "", birth, departe);
                                userList.add(user);
                            }
                        }

                        // Pass the user list to the adapter and set it to the RecyclerView
                        UserAdapter userAdapter = new UserAdapter(userList);
                        recyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }
}