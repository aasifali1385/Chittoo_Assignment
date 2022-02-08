package com.aa.chittooassignment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.aa.chittooassignment.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String authId;
    private static final int RC_SIGN_IN = 243;
    GoogleSignInClient mGoogleSignInClient;

    ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        bind.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bind.editnumber.getText().toString().trim().isEmpty() || bind.editname.getText().toString().isEmpty())
                    Toast.makeText(MainActivity.this, "Please enter Name and Mobile Number", Toast.LENGTH_SHORT).show();

                else if (bind.editnumber.getText().toString().trim().length() != 10)
                    Toast.makeText(MainActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();

                else
                    send_otp_to_number(bind.editnumber.getText().toString());
            }
        });

        bind.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bind.otp1.getText().toString().trim().isEmpty() ||
                        bind.otp2.getText().toString().trim().isEmpty() ||
                        bind.otp3.getText().toString().trim().isEmpty() ||
                        bind.otp4.getText().toString().trim().isEmpty() ||
                        bind.otp5.getText().toString().trim().isEmpty() ||
                        bind.otp6.getText().toString().trim().isEmpty())
                    Toast.makeText(MainActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                else
                    verify_otp();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        bind.googleSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Log.e("main", "Log in Successful...");
            startActivity(new Intent(MainActivity.this, TabActivity.class));
            finish();
        }
    }

    private void send_otp_to_number(String number){
        bind.sendBtn.setVisibility(View.INVISIBLE);
        bind.progressBar.setVisibility(View.VISIBLE);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {}

            @Override
            public void onVerificationFailed(FirebaseException e) {
                bind.progressBar.setVisibility(View.GONE);
                bind.sendBtn.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                authId = verificationId;
                bind.otptxt.setText("Enter OTP below, Sent to  +91 " + number);
                bind.verifytxt.setVisibility(View.VISIBLE);

                bind.linearviewname.setVisibility(View.INVISIBLE);
                bind.linearviewno.setVisibility(View.INVISIBLE);
                bind.linearviewotp.setVisibility(View.VISIBLE);

                bind.progressBar.setVisibility(View.INVISIBLE);
                bind.verifyBtn.setVisibility(View.VISIBLE);

                bind.ortxt.setVisibility(View.INVISIBLE);
                bind.googleSignBtn.setVisibility(View.INVISIBLE);

                bind.otp1.requestFocus();
                auto_otp_enter();

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verify_otp(){
        bind.verifyBtn.setVisibility(View.INVISIBLE);
        bind.progressBar.setVisibility(View.VISIBLE);

        String code = bind.otp1.getText().toString().trim() +bind.otp2.getText().toString().trim() +bind.otp3.getText().toString().trim() +
               bind.otp4.getText().toString().trim() +bind.otp5.getText().toString().trim() +bind.otp6.getText().toString().trim();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(authId, code);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("main", "Task Success........");

                            DatabaseReference dr_user = FirebaseDatabase.getInstance().getReference("AllUser").child(mAuth.getCurrentUser().getUid());
                            dr_user.child("Name").setValue(bind.editname.getText().toString());

                            startActivity(new Intent(MainActivity.this, TabActivity.class));
                            finish();

                        } else { Log.e("main", "Not Success........");
                            bind.progressBar.setVisibility(View.INVISIBLE);
                            bind.verifyBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Wrong OTP Enter", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e("main",e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("main", "Task Success........");


                            DatabaseReference dr_user = FirebaseDatabase.getInstance().getReference("AllUser").child(mAuth.getCurrentUser().getUid());
                            dr_user.child("Name").setValue(task.getResult().getUser().getDisplayName());

                            startActivity(new Intent(MainActivity.this, TabActivity.class));
                            finish();
                        } else {
                            Log.e("main", "Not Success........");
                            Toast.makeText(MainActivity.this, "Authentication failed !\nCheck Your Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void auto_otp_enter() {
        bind.otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1 )
                    bind.otp2.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        bind.otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1 )
                    bind.otp3.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        bind.otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1 )
                    bind.otp4.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        bind.otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1 )
                    bind.otp5.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        bind.otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1 )
                    bind.otp6.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

}