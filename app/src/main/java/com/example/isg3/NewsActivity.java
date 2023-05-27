package com.example.isg3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    Button adminbutton;
    RecyclerView recyclerView;
    List<StoreClass> storeList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    MyStore store;
    FirebaseAuth auth;
    SearchView searchView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().setTitle("News");
        auth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        adminbutton = findViewById(R.id.adminbutton);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(NewsActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layou);
        AlertDialog dialog = builder.create();
        dialog.show();


        storeList = new ArrayList<>();
        store = new MyStore(NewsActivity.this,storeList);
        recyclerView.setAdapter(store);

        databaseReference = FirebaseDatabase.getInstance().getReference("ISG News");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storeList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    StoreClass storeClass = itemSnapshot.getValue(StoreClass.class);
                    storeClass.setKey(itemSnapshot.getKey());
                    storeList.add(storeClass);
                }
                Collections.reverse(storeList);
                store.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });



        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(NewsActivity.this, "Something went wrong, no data for now", Toast.LENGTH_SHORT).show();
        } else {

            // Retrieve the admin status for the current user
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Registered users").child(firebaseUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean isAdmin = dataSnapshot.child("isAdmin").getValue(Boolean.class);

                        if (isAdmin != null && isAdmin) {
                            adminbutton.setVisibility(View.VISIBLE);
                        } else {
                            adminbutton.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });

        }

        adminbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, CreateNewsActivity.class);
                startActivity(intent);
            }
        });



    }


    public void searchList(String text){
        ArrayList<StoreClass> searchList = new ArrayList<>();
        for (StoreClass storeClass : storeList){
            if (storeClass.getDataTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(storeClass);
            }
        }
        store.searchDataList(searchList);
    }

    @Override
    public void onBackPressed() {
        // Start the Student activity
        Intent intent = new Intent(NewsActivity.this, student.class);
        startActivity(intent);

        // Call the super method to allow the default back button behavior
        super.onBackPressed();
    }
}