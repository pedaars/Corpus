package com.example.firebaseauth;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

public class CreatePropertyActivity4 extends AppCompatActivity implements View.OnClickListener {

    String pathToFile;
    Button btnTakePic;
    ImageView imageView;
    private String propertyId;
    private Spinner spinnerRoomName;
    private EditText editTextRoomName;
    private EditText editTextRoomLengthF;
    private EditText editTextRoomLengthI;
    private EditText editTextRoomWidthF;
    private EditText editTextRoomWidthI;
    private EditText editTextDesc;
    private StorageReference mStorageRef;
    private String mCurrentPhotoPath;
    private Uri photoURI;
    static String imageUrl;
    private ProgressBar progressBar;
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
        setContentView(R.layout.activity_create_property4);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        propertyId = Objects.requireNonNull(extras).getString("propertyId");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        spinnerRoomName = findViewById(R.id.room_name_spin);
        editTextRoomName = findViewById(R.id.room_name_e);
        editTextRoomLengthF = findViewById(R.id.room_length_f);
        editTextRoomLengthI = findViewById(R.id.room_length_i);
        editTextRoomLengthI.setFilters(new InputFilter[]{new InputFilterMinMax("0", "11")});
        editTextRoomWidthF = findViewById(R.id.room_width_f);
        editTextRoomWidthI = findViewById(R.id.room_width_i);
        editTextRoomWidthI.setFilters(new InputFilter[]{new InputFilterMinMax("0", "12")});
        editTextDesc = findViewById(R.id.editText_desc);

        findViewById(R.id.btn_custom).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        btnTakePic = findViewById(R.id.btn_camera);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions()) {
                    //Toasty.success(HomeActivity.this, "Permissions Already Granted", Toast.LENGTH_LONG, true).show();
                    dispatchPictureTakerAction();
                }
            }
        });
        imageView = findViewById(R.id.image_view);

        Glide.with(this)
                .load(R.drawable.image_placeholder)
                .into(imageView);
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
                Toasty.success(CreatePropertyActivity4.this, "Permissions granted", Toast.LENGTH_LONG, true).show();
                dispatchPictureTakerAction();
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

    private void dispatchPictureTakerAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {

            File photoFile;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(CreatePropertyActivity4.this, "com.example.firebaseauth.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);

                Glide.with(this)
                        .load(photoURI)
                        .into(imageView);
            }
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Excep : " + e.toString());
        }
        mCurrentPhotoPath = Objects.requireNonNull(image).getAbsolutePath();
        return image;
    }

    private boolean validInputs(String setRoomName, String customRoomName, String feetLength, String inchesLength, String feetWidth, String inchesWidth) {
        if (setRoomName.equals("Room Name") && spinnerRoomName.getVisibility() == View.VISIBLE) {
            TextView errorText = (TextView) spinnerRoomName.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.selection_required));
            return true;
        }

        if (customRoomName.isEmpty() && editTextRoomName.getVisibility() == View.VISIBLE) {
            editTextRoomName.setError("Room Name required");
            editTextRoomName.requestFocus();
            return true;
        }

        if (feetLength.isEmpty()) {
            editTextRoomLengthF.setError("Feet measurement required");
            editTextRoomLengthF.requestFocus();
            return true;
        }

        if (inchesLength.isEmpty()) {
            editTextRoomLengthI.setError("Inches measurement required");
            editTextRoomLengthI.requestFocus();
            return true;
        }

        if (feetWidth.isEmpty()) {
            editTextRoomWidthF.setError("Feet measurement required");
            editTextRoomWidthF.requestFocus();
            return true;
        }

        if (inchesWidth.isEmpty()) {
            editTextRoomWidthI.setError("Inches measurement required");
            editTextRoomWidthI.requestFocus();
            return true;
        }
        return false;
    }

    private void saveData() {

        String setRoomName = spinnerRoomName.getSelectedItem().toString().trim().replace(",","");;
        String customRoomName = editTextRoomName.getText().toString().trim().replace(",","");;
        String feetLength = editTextRoomLengthF.getText().toString().trim().replace(",","");;
        String inchesLength = editTextRoomLengthI.getText().toString().trim().replace(",","");;
        String feetWidth = editTextRoomWidthF.getText().toString().trim().replace(",","");;
        String inchesWidth = editTextRoomWidthI.getText().toString().trim().replace(",","");;
        final String roomDesc = editTextDesc.getText().toString().trim().replace(",","");;

        if (!validInputs(setRoomName, customRoomName, feetLength, inchesLength, feetWidth, inchesWidth)) {

            progressBar.setVisibility(View.VISIBLE);
            final String roomName;

            if (customRoomName.isEmpty()) {
                roomName = spinnerRoomName.getSelectedItem().toString().trim().replace(",","");;
            } else {
                roomName = editTextRoomName.getText().toString().trim().replace(",","");;
            }
            //Log.d("ROOM", roomName);

            final String length = "" + feetLength + "'" + inchesLength + "";
            final String width = "" + feetWidth + "'" + inchesWidth + "";

            final StorageReference filepath = mStorageRef.child("Property Images").child(propertyId).child(roomName);
            final String TAG = CreatePropertyActivity4.class.getSimpleName();

            // Register observers to listen for when the download is done or if it fails
            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = String.valueOf(uri);
                            Log.d(TAG, "onSuccess: uri= " + imageUrl);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            final CollectionReference propertyRef = db.collection("Properties");

                            final Room room = new Room(
                                    roomName, length, width, imageUrl, roomDesc
                            );


                            propertyRef.whereEqualTo("propertyId", propertyId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                            propertyRef.document(doc.getId()).collection("Rooms").add(room);

                                            progressBar.setVisibility(View.GONE);

                                            Log.d(TAG, "Firebase Success= " + imageUrl);
                                            Toasty.success(CreatePropertyActivity4.this, "Property Saved - Creation Complete", Toast.LENGTH_LONG, true).show();
                                            exProperties();
                                        }
                                    } else {
                                        Toasty.error(CreatePropertyActivity4.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveData();
                break;
            case R.id.btn_add:
                addRoom();
                break;
            case R.id.btn_custom:
                customText();
                break;
            case R.id.btn_cancel:
                cancelCustom();
                break;
        }
    }

    private void exProperties() {
        Intent intent = new Intent(CreatePropertyActivity4.this, ExistingPropertiesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void customText() {
        Toast toast = Toasty.info(CreatePropertyActivity4.this, "Please Input Custom Room Name", Toast.LENGTH_LONG, true);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        Button customButton = findViewById(R.id.btn_custom);
        Button cancelButton = findViewById(R.id.btn_cancel);

        spinnerRoomName.setVisibility(View.INVISIBLE);
        editTextRoomName.setVisibility(View.VISIBLE);

        customButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
    }

    private void cancelCustom() {
        Toast toast = Toasty.info(CreatePropertyActivity4.this, "Please Select Room Name", Toast.LENGTH_LONG, true);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        Button customButton = findViewById(R.id.btn_custom);
        Button cancelButton = findViewById(R.id.btn_cancel);

        spinnerRoomName.setVisibility(View.VISIBLE);
        editTextRoomName.setVisibility(View.INVISIBLE);

        customButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
    }

    private void addRoom() {
        progressBar.setVisibility(View.VISIBLE);

        String setRoomName = spinnerRoomName.getSelectedItem().toString().trim();
        String customRoomName = editTextRoomName.getText().toString().trim();
        String feetLength = editTextRoomLengthF.getText().toString().trim();
        String inchesLength = editTextRoomLengthI.getText().toString().trim();
        String feetWidth = editTextRoomWidthF.getText().toString().trim();
        String inchesWidth = editTextRoomWidthI.getText().toString().trim();
        final String roomDesc = editTextDesc.getText().toString().trim();

        if (!validInputs(setRoomName, customRoomName, feetLength, inchesLength, feetWidth, inchesWidth)) {

            final String roomName;

            if (customRoomName.isEmpty()) {
                roomName = spinnerRoomName.getSelectedItem().toString().trim();
            } else {
                roomName = editTextRoomName.getText().toString().trim();
            }
            //Log.d("ROOM", roomName);

            final String length = "" + feetLength + " ft" + " X " + inchesLength + " in" + "";
            final String width = "" + feetWidth + " X " + inchesWidth + "";

            final StorageReference filepath = mStorageRef.child("Property Images").child(propertyId).child(roomName);
            final String TAG = CreatePropertyActivity4.class.getSimpleName();

            // Register observers to listen for when the download is done or if it fails
            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = String.valueOf(uri);
                            Log.d(TAG, "onSuccess: uri= " + imageUrl);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            final CollectionReference propertyRef = db.collection("Properties");

                            final Room room = new Room(
                                    roomName, length, width, imageUrl, roomDesc
                            );

                            propertyRef.whereEqualTo("propertyId", propertyId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                            propertyRef.document(doc.getId()).collection("Rooms").add(room);

                                            progressBar.setVisibility(View.GONE);

                                            Log.d(TAG, "Firebase Success= " + imageUrl);
                                            Toasty.success(CreatePropertyActivity4.this, "Room added to Property", Toast.LENGTH_LONG, true).show();

                                            Intent intent = new Intent(CreatePropertyActivity4.this, CreatePropertyActivity4.class);
                                            String propId = propertyId;
                                            intent.putExtra("propertyId", propId);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Toasty.error(CreatePropertyActivity4.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.account_link);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent homeIntent = new Intent(this, ExistingPropertiesActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}