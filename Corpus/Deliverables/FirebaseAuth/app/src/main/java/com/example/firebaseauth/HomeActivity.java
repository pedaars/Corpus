package com.example.firebaseauth;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 1240;

    String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button ExistingCust = findViewById(R.id.ex_cust);
        Button props = findViewById(R.id.prop);
        Button Logout = findViewById(R.id.signOut);
        ExistingCust.setOnClickListener(this);
        props.setOnClickListener(this);
        Logout.setOnClickListener(this);

        if (checkAndRequestPermissions()) {

            //Toasty.success(HomeActivity.this, "Permissions Already Granted", Toast.LENGTH_LONG, true).show();
        }
    }

    private boolean checkAndRequestPermissions() {
        // Check which permissions are granted
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        // Ask for required permissions
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE
            );
            return false;
        }

        // All permissions granted
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            // Gather permission results
            for (int i = 0; i < grantResults.length; i++) {

                // Add only permissions that are denied
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            //Check that all permissions have been granted
            if (deniedCount == 0) {
                Toasty.success(HomeActivity.this, "Permissions granted", Toast.LENGTH_LONG, true).show();
                startApp();
            }

            // If any permissions have been denied
            else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    // Permission is denied (first time with "never ask again" not checked)
                    // ask again with explanation of usage
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        // Show explanation dialog
                        showDialog("", "This app needs Location, Camera and Storage permissions to function correctly",
                                "Yes, Grant permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        checkAndRequestPermissions();
                                    }
                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                    }
                    // Permission denied (never ask again IS checked)
                    else {
                        // Ask user to open settings to manually configure permissions
                        showDialog("", "You have denied permissions. Please allow all at [Settings} -> {Apps} -> {Property Manager} -> [Permissions}",
                                "Go to settings",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        // go to app settings
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                        break;
                    }
                }
            }
        }
    }

    private AlertDialog showDialog(String title, String msg, String positiveLabel,
                                    DialogInterface.OnClickListener positiveOnClick,
                                    String negativeLabel, DialogInterface.OnClickListener negativeOnClick,
                                    boolean isCancelAble) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    private void startApp() {
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.account_link:
                accInfo();
                return(true);
            case R.id.about_link:
                accInfo();
                return(true);
            case R.id.faq_link:
                accInfo();
                return(true);
        }
            return(super.onOptionsItemSelected(item));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ex_cust:
                exCustomer();
                break;
            case R.id.prop:
                exProps();
                break;
            case R.id.signOut:
                signOut();
                break;
        }
    }

    private void exCustomer() {
       Intent intent = new Intent(HomeActivity.this, ExistingCustomersActivity.class);
       startActivity(intent);
    }

    private void exProps() {
        Intent intent = new Intent(HomeActivity.this, ExistingPropertiesActivity.class);
        startActivity(intent);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(
                                    HomeActivity.this,
                                    FireBaseAuthActivity.class));
                            finish();
                        } else {
                            // Report error to user
                            Toasty.error(HomeActivity.this, "Error Signing Out", Toast.LENGTH_LONG, true).show();
                        }
                    }
                });
    }

    private void accInfo() {
        Intent intent = new Intent(HomeActivity.this, SignedInActivity.class);
        startActivity(intent);
    }
}
