package com.example.firebaseauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

public class FireBaseAuthActivity extends AppCompatActivity {
    private static final String TAG = FireBaseAuthActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_auth);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            authenticateUser();
        }
    }

    private void authenticateUser() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.CustomTheme)
                        .setLogo(R.drawable.property_manager)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(true)
                        .build(),
                REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                assert response != null;
                startActivity(new Intent(this, HomeActivity.class)
                .putExtra("my_token", response.getIdpToken()));
            }
        } else {
            // Sign in failed
            if (response == null) {
                // User cancelled Sign-in
                displayMessage("Sign in cancelled");
                return;
            }

            if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                // Device has no network connection
                displayMessage("No internet connection");
                return;
            }

            // Unknown error occurred
            displayMessage("Unknown error");
            Log.e(TAG, "Sing-in error: ", response.getError());
        }
    }

    private void displayMessage(String message){
        Toasty.error(this, message, Toast.LENGTH_LONG, true).show();
    }
}