package com.example.firebaseauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class SignedInActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
        Button Logout = findViewById(R.id.signOut);
        Button DeleteUser = findViewById(R.id.deleteAccount);
        Logout.setOnClickListener(this);
        DeleteUser.setOnClickListener(this);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, FireBaseAuthActivity.class));
            finish();
            return;
        }

        TextView email = findViewById(R.id.email);
        TextView displayName = findViewById(R.id.displayname);

        email.setText(String.format("Email - %s", currentUser.getEmail()));
        displayName.setText(String.format("Name - %s", currentUser.getDisplayName()));
        String imageUri = String.valueOf(currentUser.getPhotoUrl());

        if (currentUser.getPhotoUrl() != null) {
            ImageView profile = findViewById(R.id.imageView_room);
            Picasso.get().load(imageUri).into(profile);
        } else {
            ImageView profile = findViewById(R.id.imageView_room);
            profile.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.account_link);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signOut:
                signOut();
                break;
            case R.id.deleteAccount:
                deleteAccount();
                break;
        }
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(
                                    SignedInActivity.this,
                                    FireBaseAuthActivity.class));
                            finish();
                        } else {
                            // Report error to user
                            displayMessage("Error Signing out");
                        }
                    }
                });
    }

    public void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignedInActivity.this,
                                    FireBaseAuthActivity.class));
                            finish();
                        } else {
                            // Notify user of error
                            displayMessage("Error deleting Account");
                        }
                    }
                });
    }

    private void displayMessage(String message) {
        Toasty.error(this, message, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent homeIntent = new Intent(this, HomeActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}

