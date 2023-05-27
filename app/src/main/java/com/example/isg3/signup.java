package com.example.isg3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {
    EditText editText1, editText2,editText3,editText4,editText5, editbirth , editDepart;
    AppCompatButton buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView sign;
    private static final String TAG="signup";

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(signup.this,student.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Sign up");
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        editText1 = findViewById(R.id.firstname);

        editText3 = findViewById(R.id.email);
        editText4 = findViewById(R.id.password);
        editText5 = findViewById(R.id.password2);
        buttonReg = findViewById(R.id.button);
        sign = findViewById(R.id.signin);
        editbirth = findViewById(R.id.editbirth);
        editDepart = findViewById(R.id.editdepart);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String firstname  , email , password , password1 , birthday, departement;
                firstname =String.valueOf( editText1.getText());
                email = String.valueOf( editText3.getText());
                birthday = String.valueOf(editbirth.getText());
                departement = String.valueOf(editDepart.getText());
                password = String.valueOf( editText4.getText());
                password1 = String.valueOf( editText5.getText());


                if (TextUtils.isEmpty(firstname)){
                    Toast.makeText(signup.this,"Add Your First Name",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(signup.this,"Add Your Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(signup.this,"Add Your Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password1)){
                    Toast.makeText(signup.this,"Confirm Your Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(password1)) {
                    Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(birthday)){
                    Toast.makeText(signup.this,"Add Your Birthday",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(departement)){
                    Toast.makeText(signup.this,"Add Your Departement",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    registerUser(firstname,email,password,birthday,departement);
                }







            }

            private void registerUser(String firstname, String email, String password, String birthday, String departement) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                //Create User Profile



                auth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(signup.this, "User Created ", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //Enter user Data
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(firstname, email, password,birthday,departement);

                            //User Reference
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(signup.this,student.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(signup.this, "user registration failed", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });



                        }else {
                            try {
                                throw task.getException();
                            }catch(FirebaseAuthWeakPasswordException e){
                                editText4.setError("Your Password is too weak");
                                editText4.requestFocus();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                editText4.setError("Your email is invalid or already in use");
                                editText4.requestFocus();
                            }catch (FirebaseAuthUserCollisionException e){
                                editText4.setError("User is already registered");
                                editText4.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });


    }

    @Override
    public void onBackPressed() {
        // Start the Student activity
        Intent intent = new Intent(signup.this, login.class);
        startActivity(intent);

        // Call the super method to allow the default back button behavior
        super.onBackPressed();
    }
}