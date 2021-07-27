package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.lazyworkout.R;
import com.example.lazyworkout.api.AuthenticationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout txtInputFieldEmail;
    private TextInputEditText inputEmail;
    private Button resetPasswordBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        resetPasswordBtn.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        }
    }

    private void initViews() {
        txtInputFieldEmail = findViewById(R.id.forgotTextFieldEmail);

        inputEmail = findViewById(R.id.forgotInputEmail);

        resetPasswordBtn = findViewById(R.id.forgotResetPasswordBtn);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.forgotResetPasswordBtn):
                resetPassword();
                break;
            default:
                break;
        }
    }

    private void resetPassword() {
        if (!validateEmail()) {
            return;
        } else {
            String email = inputEmail.getText().toString();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(ForgotPasswordActivity.this)
                                .setTitle("Email Sent Successfully")
                                .setMessage("Last step: Please check your email to reset the passsword!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                    }
                                }).setCancelable(false);
                        alert.show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "No email found, please try again!",
                                Toast.LENGTH_LONG).show();
                        inputEmail.setText("");
                    }
                }
            });
        }
    }

    private boolean validateEmail() {

        String email = inputEmail.getText().toString().trim();

        String authenticate = AuthenticationHelper.validateEmail(email);
        if (authenticate != null) {
            txtInputFieldEmail.setError(authenticate);
            return false;
        } else {
            txtInputFieldEmail.setError(null);
            txtInputFieldEmail.setErrorEnabled(false);
            return true;
        }
    }
}