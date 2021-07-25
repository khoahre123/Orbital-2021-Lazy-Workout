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
import com.example.lazyworkout.api.AuthenticationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    public LoginActivity() {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
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

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void initViews() {

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

        String email = getEmail();
        String password = getPassword();

        if (!(validateEmail(email) && validatePassword(password))) {
//            inputEmail.setText("");
//            inputPassword.setText("");
            return;
        } else {

            final Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        boolean isFirstTimeUser = (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp());

                        if (user.isEmailVerified()) {
                            if (isFirstTimeUser) {
                                startActivity(new Intent(LoginActivity.this, TutorialActivity.class));
                            } else {
                                // TODO: just setting locksetting first
                                startActivity(new Intent(LoginActivity.this, TutorialActivity.class));
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

                        Log.d("Error", task.getException().getMessage());
                        Snackbar snackbar = Snackbar.make(loginView, task.getException().getMessage(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Try Again", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        inputEmail.setText("");
                                        inputPassword.setText("");
                                    }
                                });

                        View snackbarView = snackbar.getView();
                        TextView tv = (TextView) snackbarView.findViewById(R.id.snackbar_text);
                        tv.setMaxLines(3);
                        snackbar.show();
                    }
                }
            });
        }

    }

    public String getEmail() {
        String email = inputEmail.getText().toString().trim();
        return email;
    }

    public boolean validateEmail(String email) {

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

    public String getPassword() {
        String password = inputPassword.getText().toString().trim();
        return password;
    }

    public boolean validatePassword(String password) {

        String authenticate = AuthenticationHelper.validatePassword(password);
        if (authenticate != null) {
            txtInputFieldPassword.setError("Password is required");
            return false;
        } else {
            txtInputFieldPassword.setError(null);
            txtInputFieldPassword.setErrorEnabled(false);
            return true;
        }
    }

}