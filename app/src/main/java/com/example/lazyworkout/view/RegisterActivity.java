package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyworkout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private ImageView logo;
    private TextView txtSignup, txtPaT;
    private TextInputLayout txtInputFieldName, txtInputFieldEmail, txtInputFieldPassword;
    private TextInputEditText inputName, inputEmail, inputPassword;
    private Button registerBtn;
    private RelativeLayout registerView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        logo.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        txtSignup.setOnClickListener(this);
        txtPaT.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        }


    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        logo = findViewById(R.id.registerLogo);

        txtInputFieldName = findViewById(R.id.registerInputFieldName);
        txtInputFieldEmail = findViewById(R.id.registerInputFieldEmail);
        txtInputFieldPassword = findViewById(R.id.registerInputFieldPw);

        inputName = findViewById(R.id.registerInputName);
        inputEmail = findViewById(R.id.registerInputEmail);
        inputPassword = findViewById(R.id.registerInputPw);

        registerBtn = findViewById(R.id.registerCreateAccountBtn);

        SpannableString ss = new SpannableString("Already have an account? Sign up");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 25,32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtSignup = findViewById(R.id.registerSignup);
        txtSignup.setText(ss);
        txtSignup.setMovementMethod(LinkMovementMethod.getInstance());

        txtPaT = findViewById(R.id.registerPaT);

        registerView = findViewById(R.id.registerView);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.registerLogo):
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case (R.id.registerCreateAccountBtn):
                registerUser();
                break;
            case (R.id.registerSignup):
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case (R.id.registerPaT):
                Log.d("To be implemented", "privacy and term");
            default:
                break;
        }
    }

    private void registerUser() {

        if (!(validateUsername() && validateEmail() && validatePassword())) {
            return;
        } else {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                user.sendEmailVerification();
                                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(RegisterActivity.this)
                                        .setTitle("Register Successful")
                                        .setMessage("Last step: Verify your email before logging in with your new account!")
                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            }
                                        }).setCancelable(false);;
                                alert.show();

                            } else {
                                Log.w(TAG, task.getException().getMessage());

                                Snackbar snackbar = Snackbar.make(registerView, task.getException().getMessage(), Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Try Again", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                inputName.setText("");
                                                inputEmail.setText("");
                                                inputPassword.setText("");
                                            }
                                        });
                                snackbar.setActionTextColor(getResources().getColor(R.color.red));
                                View snackbarView = snackbar.getView();
                                TextView tv = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                tv.setMaxLines(3);
                                snackbar.show();

                            }
                        }
                    });

        }


    }

    private boolean validateUsername() {
        String username = inputName.getText().toString().trim();

        if (username.isEmpty()) {
            txtInputFieldName.setError("Username is required");
            return false;
        } else {
            txtInputFieldName.setError(null);
            txtInputFieldName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty()) {
            txtInputFieldEmail.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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