package com.vdsl.myapplication.Profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vdsl.myapplication.Account.Activity.ActivitySignIn;
import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class FragmentProfile extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());

        binding.imgSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), ActivitySignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });


        binding.imgSignOut.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout your accont");
            builder.setMessage("Are you sure to log out?");

            builder.setNegativeButton("NO", (dialog, which) -> builder.create().cancel());

            builder.setPositiveButton("YES", (dialog, which) -> {
                progressDialog.setMessage("Loging out...");
                progressDialog.show();

                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            if (requireActivity() != null) {
                                Intent intent = new Intent(requireActivity(), ActivitySignIn.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        }
                    }
                });
            });

            builder.create().show();

        });
        //end
    }
}