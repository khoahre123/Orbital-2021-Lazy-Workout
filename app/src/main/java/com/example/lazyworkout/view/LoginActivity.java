package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lazyworkout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.lazyworkout.view.RegisterActivity;
import com.example.lazyworkout.view.ProfileActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private TextInputLayout txtInputFieldEmail, txtInputFieldPassword;
    private TextInputEditText inputEmail, inputPassword;
    private TextView forgotPassword;
    private Button loginBtn, createAccountBtn;
    private RelativeLayout loginView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        forgotPassword.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        createAccountBtn.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.blue));
        }


    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        txtInputFieldEmail = findViewById(R.id.loginTextFieldEmail);
        txtInputFieldPassword = findViewById(R.id.loginTextFieldPassword);

        inputEmail = findViewById(R.id.loginInputEmail);
        inputPassword = findViewById(R.id.loginInputPassword);

        forgotPassword = findViewById(R.id.loginForgotPw);

        loginBtn = findViewById(R.id.loginLoginBtn);
        createAccountBtn = findViewById(R.id.loginCreatAccountBtn);

        loginView = findViewById(R.id.loginView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.loginForgotPw):
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case (R.id.loginLoginBtn):
                userLogin();
                break;
            case (R.id.loginCreatAccountBtn):
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    private void userLogin() {

        if (!(validateEmail() && validatePassword())) {
            return;
        } else {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            final Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        boolean isFirstTimeUser = (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp());

                        if (user.isEmailVerified()) {
                            if (isFirstTimeUser) {
                                startActivity(new Intent(LoginActivity.this, LockSettingActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            }

                        } else {

                            user.sendEmailVerification();
                            Snackbar snackbar = Snackbar.make(loginView, "Please verify your email first." +
                                    "We have resent the verification email", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            inputEmail.setText("");
                                            inputPassword.setText("");
                                        }
                                    });
                            snackbar.show();
                            mAuth.signOut();

                        }

                    } else {
                        Snackbar snackbar = Snackbar.make(loginView, task.getException().getMessage(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Try Again", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        inputEmail.setText("");
                                        inputPassword.setText("");
                                    }
                                });

                        View snackbarView = snackbar.getView();
                        TextView tv = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setMaxLines(3);
                        snackbar.show();
                    }
                }
            });
        }

    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty()) {
            txtInputFieldEmail.setError("Email is required");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtInputFieldEmail.setError("The email is invalid");
            return false;
        } else {
            txtInputFieldEmail.setError(null);
            txtInputFieldEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = inputPassword.getText().toString().trim();

        if (password.isEmpty()) {
            txtInputFieldPassword.setError("Password is required");
            return false;
        } else {
            txtInputFieldPassword.setError(null);
            txtInputFieldPassword.setErrorEnabled(false);
            return true;
        }
    }

}