package com.example.isg3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyEvents extends AppCompatActivity {


    private RecyclerView recyclerView;
    MyAdapter adapter;
    List<DataClass> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        getSupportActionBar().setTitle("My Events");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        adapter = new MyAdapter(MyEvents.this,dataList);
        recyclerView.setAdapter(adapter);

        // Retrieve the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

// Query all events
            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Android Tutorials");
            eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            DataClass event = eventSnapshot.getValue(DataClass.class);
                            if (event != null) {
                                // Check if the current user ID exists in the postuledBy field
                                List<String> postuledByList = event.getPostuledBy();
                                if (postuledByList != null && postuledByList.contains(userId)) {
                                    dataList.add(event);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MyEvents.this, "No events found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MyEvents.this, "Failed to retrieve events", Toast.LENGTH_SHORT).show();
                }
            });





        }
    }

    @Override
    public void onBackPressed() {
        // Start the Student activity
        Intent intent = new Intent(MyEvents.this, student.class);
        startActivity(intent);

        // Call the super method to allow the default back button behavior
        super.onBackPressed();
    }

}