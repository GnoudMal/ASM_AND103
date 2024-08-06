package com.vdsl.myapplication.Account.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivitySignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        binding.txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(ActivitySignUp.this, ActivitySignIn.class));
        });

        binding.btnSignUp.setOnClickListener(v -> {
            String userName = binding.edtUsername.getText().toString().trim();
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String rePassword = binding.edtRePassword.getText().toString().trim();

            boolean error = false;

            if (userName.isEmpty()) {
                binding.edtUsername.setError("Please enter your User Name!");
                error = true;
            }
            if (email.isEmpty()) {
                binding.edtEmail.setError("Please enter your Email!");
                error = true;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.setError("Wrong email format!");
                error = true;
            }
            if (password.isEmpty()) {
                binding.edtPassword.setError("Please enter your Password!");
                error = true;
            } else if (password.length() < 6) {
                binding.edtPassword.setError("Password must be at least 6 characters!");
                error = true;
            }
            if (rePassword.isEmpty()) {
                binding.edtRePassword.setError("Please confirm your Password!");
                error = true;
            } else if (!rePassword.equals(password)) {
                binding.edtRePassword.setError("Password not match, please enter again!");
                error = true;
            }

            if (!error) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("uid", uid);
                        user.put("username", userName);
                        user.put("email", email);
                        user.put("role", "user");
                        db.collection("Users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, ActivitySignIn.class));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to add user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            // Email đã tồn tại
                            Toast.makeText(this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Các lỗi khác
                            Toast.makeText(this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

    }
}