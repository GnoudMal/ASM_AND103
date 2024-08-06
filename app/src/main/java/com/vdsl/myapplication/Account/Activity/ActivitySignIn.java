package com.vdsl.myapplication.Account.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vdsl.myapplication.Home.Activity.ActivityHome;
import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class ActivitySignIn extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth userAuth;

    SharedPreferences sharedPreferences;
    /** @noinspection deprecation*/
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        sharedPreferences = this.getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);

        binding.txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(ActivitySignIn.this, ActivitySignUp.class));
        });
        binding.txtReset.setOnClickListener(v -> {
            startActivity(new Intent(ActivitySignIn.this, ActivityForgotPassword.class));
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                boolean error = false;
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

                if (!error) {
                    userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ActivitySignIn.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ActivitySignIn.this, ActivityHome.class));
                            finish();
                        } else {
                            Exception exception = task.getException();
//                                    if (exception instanceof FirebaseAuthInvalidUserException ) {
//                                        // Email không tồn tại
//                                        Log.e("SignInError", "Email does not exist: " + exception.getMessage());
//                                        binding.edtEmail.setError("Email does not exist!");
//                                        binding.edtEmail.requestFocus();
//                                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
//                                // Mật khẩu không đúng
//                                Log.e("SignInError", "Incorrect password: " + exception.getMessage());
//                                binding.edtPassword.setError("Incorrect password!");
//                                binding.edtPassword.requestFocus();
//                            } else {
                            // Xử lý các trường hợp lỗi khác
                            Toast.makeText(ActivitySignIn.this, "Failed! wrong email or password!", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    })
//                            .addOnFailureListener(e -> {
//                                if (e instanceof FirebaseAuthInvalidUserException) {
//                                    binding.edtEmail.setError("Email does not exist!");
//                                    Log.e("SignInError", "Email does not exist: " + e.getMessage());
//                                    binding.edtEmail.requestFocus();
//                                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                                    Log.e("SignInError", "Incorrect password: " + e.getMessage());
//                                    binding.edtPassword.setError("Incorrect password!");
//                                    binding.edtPassword.requestFocus();
//                                } else {
//                                    Toast.makeText(ActivitySignIn.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
                    ;
                }
            }
        });


    }
}


//
//
//
//package com.vdsl.myapplication.Account.Activity;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.vdsl.myapplication.Home.Activity.ActivityHome;
//import com.vdsl.myapplication.R;
//import com.vdsl.myapplication.databinding.ActivitySignInBinding;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
//import com.google.firebase.auth.FirebaseAuthInvalidUserException;
//import com.google.firebase.auth.FirebaseAuthUserCollisionException;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class ActivitySignIn extends AppCompatActivity {
//
//    ActivitySignInBinding binding;
//    FirebaseAuth userAuth;
//    FirebaseFirestore firestore;
//
//    SharedPreferences sharedPreferences;
//    /** @noinspection deprecation*/
//    ProgressDialog progressDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        binding = ActivitySignInBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        userAuth = FirebaseAuth.getInstance();
//        firestore = FirebaseFirestore.getInstance();
//        progressDialog = new ProgressDialog(this);
//        sharedPreferences = this.getSharedPreferences("User", Context.MODE_PRIVATE);
//
//        binding.txtRegister.setOnClickListener(v -> {
//            startActivity(new Intent(ActivitySignIn.this, ActivitySignUp.class));
//        });
//        binding.txtReset.setOnClickListener(v -> {
//            startActivity(new Intent(ActivitySignIn.this, ActivityForgotPassword.class));
//        });
//
//        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = binding.edtEmail.getText().toString().trim();
//                String password = binding.edtPassword.getText().toString().trim();
//
//                boolean error = false;
//                if (email.isEmpty()) {
//                    binding.edtEmail.setError("Please enter your Email!");
//                    error = true;
//                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    binding.edtEmail.setError("Wrong email format!");
//                    error = true;
//                }
//                if (password.isEmpty()) {
//                    binding.edtPassword.setError("Please enter your Password!");
//                    error = true;
//                } else if (password.length() < 6) {
//                    binding.edtPassword.setError("Password must be at least 6 characters!");
//                    error = true;
//                }
//
//                if (!error) {
//                    progressDialog.setMessage("Signing in...");
//                    progressDialog.show();
//
//                    userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            String uid = userAuth.getCurrentUser().getUid();
//                            fetchUserInfo(uid);
//                        } else {
//                            progressDialog.dismiss();
//                            Exception exception = task.getException();
//                            Toast.makeText(ActivitySignIn.this, "Failed! wrong email or password!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    private void fetchUserInfo(String uid) {
//        firestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                progressDialog.dismiss();
//                if (task.isSuccessful() && task.getResult() != null) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        String email = document.getString("email");
//                        String role = document.getString("role");
//                        String username = document.getString("username");
//
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("email", email);
//                        editor.putString("role", role);
//                        editor.putString("uid", uid);
//                        editor.putString("username", username);
//                        editor.apply();
//
//                        Toast.makeText(ActivitySignIn.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(ActivitySignIn.this, ActivityHome.class));
//                        finish();
//                    } else {
//                        Toast.makeText(ActivitySignIn.this, "User data not found", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(ActivitySignIn.this, "Failed to retrieve user info", Toast.LENGTH_SHORT).show();
//                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
//                }
//            }
//        });
//    }
//}
