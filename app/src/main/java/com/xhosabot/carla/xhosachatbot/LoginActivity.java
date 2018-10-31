package com.xhosabot.carla.xhosachatbot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity{
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUtils mFirebaseUtils = new FirebaseUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            Log.d(TAG, "Something");
        FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "User: " + user.toString());

                Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                intent.putExtra("USER", user.getUid());
                startActivity(intent);
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                setUpAuthUILogin();
            }
        };
    }

    private void setUpAuthUILogin() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("za").build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        //.setLogo(R.drawable.login_icon)
                        //.setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);

    }


    public void submitDefaultStartMessage(String uid){
        mFirebaseUtils.getFirestore()
                .collection(uid)
                .add(new Message("Sawubona, nceda undibuze umbuzo", true));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Result cancelled");
            }
            else if (resultCode == RESULT_OK) {
                Log.d(TAG, "Result ok");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(null!=user){
                    submitDefaultStartMessage(user.getUid());
                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                    intent.putExtra("USER", user.getUid());
                    startActivity(intent);
                    finish();
                }
            } else {
                if (null == response) {
                    return;
                }

                if (ErrorCodes.NO_NETWORK == response.getError().getErrorCode()) {
                    Toast.makeText(getApplicationContext(),R.string.sign_in_no_network,Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(),R.string.sign_in_failed,Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mAuthListener)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
