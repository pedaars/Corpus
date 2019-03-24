package com.example.firebaseauth;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

public class UpdatePropertyActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName;
    private EditText editTextHouseNo;
    private EditText editTextStreet;
    private EditText editTextTown;
    private EditText editTextPostcode;

    private TextView textViewName;
    private TextView textViewHouseNo;
    private TextView textViewStreet;
    private TextView textViewTown;
    private TextView textViewPostcode;
    private TextView textViewPropertyType;
    private TextView textViewReceptions;
    private TextView textViewBathrooms;
    private TextView textViewBedrooms;

    private Spinner spinnerPropType;
    private Spinner spinnerNoReceps;
    private Spinner spinnerNoBaths;
    private Spinner spinnerNoBeds;

    private String customerId;
    private String propertyId;
    private String customerName;
    private String creatorId;

    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private Property property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_property);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);

        property = (Property) getIntent().getSerializableExtra("property");

        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.name);
        editTextHouseNo = findViewById(R.id.prop_no);
        editTextStreet = findViewById(R.id.street_name);
        editTextTown = findViewById(R.id.town);
        editTextPostcode = findViewById(R.id.postcode);

        spinnerPropType = findViewById(R.id.prop_type_spin);
        spinnerNoReceps = findViewById(R.id.no_receps_spin);
        spinnerNoBaths = findViewById(R.id.no_baths_spin);
        spinnerNoBeds = findViewById(R.id.no_beds_spin);

        textViewName = findViewById(R.id.name_v);
        textViewHouseNo = findViewById(R.id.prop_no_v);
        textViewStreet = findViewById(R.id.street_name_v);
        textViewTown = findViewById(R.id.town_v);
        textViewPostcode = findViewById(R.id.postcode_v);
        textViewPropertyType = findViewById(R.id.property_type_v);
        textViewReceptions = findViewById(R.id.receptions_v);
        textViewBathrooms = findViewById(R.id.bathrooms_v);
        textViewBedrooms = findViewById(R.id.bedrooms_v);

        editTextName.setText(property.getCustomer());
        editTextHouseNo.setText(property.getAddress().getHouseNo());
        editTextStreet.setText(property.getAddress().getStreet());
        editTextTown.setText(property.getAddress().getTown());
        editTextPostcode.setText(property.getAddress().getPostCode());

        spinnerPropType.setSelection(getIndex(spinnerPropType, property.getPropertyType()));
        spinnerNoReceps.setSelection(getIndex(spinnerNoReceps, property.getReceptions()));
        spinnerNoBaths.setSelection(getIndex(spinnerNoBaths, property.getBathrooms()));
        spinnerNoBeds.setSelection(getIndex(spinnerNoBeds, property.getBedrooms()));

        textViewName.setText(property.getCustomer());
        textViewHouseNo.setText(property.getAddress().getHouseNo());
        textViewStreet.setText(property.getAddress().getStreet());
        textViewTown.setText(property.getAddress().getTown());
        textViewPostcode.setText(property.getAddress().getPostCode());

        textViewPropertyType.setText(property.getPropertyType());
        textViewReceptions.setText(property.getReceptions());
        textViewBathrooms.setText(property.getBathrooms());
        textViewBedrooms.setText(property.getBedrooms());

        @SuppressLint("CutPasteId") TextView custNameTxt = findViewById(R.id.name_v);
        customerName = custNameTxt.getText().toString().trim();
        customerId = property.getCustomerId();
        propertyId = String.valueOf(property.getPropertyId());
        Log.d("CUSTOMERID ", propertyId);


        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_slideshow).setOnClickListener(this);

        Log.d("PROPERTYID ", propertyId);
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    private boolean validInputs(String name, String propNo, String streetName, String town, String postCode, String propertyType, String receptions, String bathrooms, String bedrooms) {
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return true;
        }

        if (propNo.isEmpty()) {
            editTextHouseNo.setError("House Number required");
            editTextHouseNo.requestFocus();
            return true;
        }

        if (streetName.isEmpty()) {
            editTextStreet.setError("Street is required");
            editTextStreet.requestFocus();
            return true;
        }

        if (town.isEmpty()) {
            editTextTown.setError("Town is required");
            editTextTown.requestFocus();
            return true;
        }

        if (postCode.isEmpty()) {
            editTextPostcode.setError("Postcode required");
            editTextPostcode.requestFocus();
            return true;
        }
        if (propertyType.equals("Choose a Property Type")) {
            TextView errorText = (TextView)spinnerPropType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.selection_required));
            return true;
        }

        if (receptions.equals("Number of Reception Rooms")) {
            TextView errorText = (TextView)spinnerNoReceps.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.selection_required));
            return true;
        }

        if (bathrooms.equals("Number of Bathrooms")) {
            TextView errorText = (TextView)spinnerNoBaths.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.selection_required));
            return true;
        }

        if (bedrooms.equals("Number of Bedrooms")) {
            TextView errorText = (TextView)spinnerNoBeds.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.selection_required));
            return true;
        }
        return false;
    }

    private void updateProperty() {

        final String name = editTextName.getText().toString().trim().replace(",","");;
        String propNo = editTextHouseNo.getText().toString().trim().replace(",","");;
        String streetName = editTextStreet.getText().toString().trim().replace(",","");;
        String town = editTextTown.getText().toString().trim().replace(",","");;
        String postCode = editTextPostcode.getText().toString().trim().replace(",","");;
        String propertyType = spinnerPropType.getSelectedItem().toString().trim().replace(",","");;
        String receptions = spinnerNoReceps.getSelectedItem().toString().trim().replace(",","");;
        String bathrooms = spinnerNoBaths.getSelectedItem().toString().trim().replace(",","");;
        String bedrooms = spinnerNoBeds.getSelectedItem().toString().trim().replace(",","");;


        if (!validInputs(name, propNo, streetName, town, postCode, propertyType, receptions, bathrooms, bedrooms)) {

            progressBar.setVisibility(View.VISIBLE);

            creatorId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();

            String mainPhotoUrl = property.getMainPhotoUrl();

            Address address = new Address();
            address.setHouseNo(propNo);
            address.setStreet(streetName);
            address.setTown(town);
            address.setPostCode(postCode);

            final Property p = new Property(
                    name, bathrooms, bedrooms, receptions, propertyType, address, creatorId,
                    customerId, propertyId, mainPhotoUrl
            );

            db.collection("Properties").document(property.getPropertyId())
                    .update(
                            "customer", p.getCustomer(),
                            "bathrooms", p.getBathrooms(),
                            "bedrooms", p.getBedrooms(),
                            "receptions", p.getReceptions(),
                            "propertyType", p.getPropertyType(),
                            "address", p.getAddress(),
                            "creatorId", p.getCreatorId()
                    )
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                //Toast.makeText(UpdatePropertyActivity.this, "T1 Property Updated", Toast.LENGTH_SHORT).show();

                                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                final CollectionReference propertiesRef = rootRef.collection("Properties");
                                final CollectionReference customersRef = rootRef.collection("Customers");
                                final String newName = p.getCustomer();
                                propertiesRef.whereEqualTo("customerId", customerId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                        if (task2.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task2.getResult())) {
                                                propertiesRef.document(document.getId()).update("customer", newName);
                                                //Toast.makeText(UpdatePropertyActivity.this, "T2 Property Updated " + newName, Toast.LENGTH_SHORT).show();

                                                customersRef.whereEqualTo("customerId", customerId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task3) {
                                                        if (task3.isSuccessful()) {
                                                            for (QueryDocumentSnapshot doc : Objects.requireNonNull(task3.getResult())) {
                                                                customersRef.document(doc.getId()).update("name", newName);
                                                                progressBar.setVisibility(View.GONE);
                                                                Toasty.success(UpdatePropertyActivity.this, newName + "'s Property Updated", Toast.LENGTH_LONG, true).show();
                                                                exCustomers();
                                                            }
                                                        } else {
                                                            Toasty.error(UpdatePropertyActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                                            Log.d("T3 Exception found ", String.valueOf(task3.getException()));
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            Toasty.error(UpdatePropertyActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                            Log.d("T2 Exception found ", String.valueOf(task2.getException()));
                                        }
                                    }
                                });

                            } else {
                                Toasty.error(UpdatePropertyActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                Log.d("T1 Exception found ", String.valueOf(task1.getException()));
                            }
                        }

                    });
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                updateProperty();
                break;
            case R.id.btn_edit:
                focusEdit();
                break;
            case R.id.btn_cancel:
                cancelUpdate();
                break;
            case R.id.btn_slideshow:
                startSlideshow();
                break;
            case R.id.btn_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleting a Property is permanent...");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProperty();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelUpdate();
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
                break;
        }
    }

    private void focusEdit() {
        Toast toast = Toasty.info(UpdatePropertyActivity.this, "Update Property details and click update to save", Toast.LENGTH_SHORT, true);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 400);
        toast.show();

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.update_property_details);

        Button editButton = findViewById(R.id.btn_edit);
        Button updateButton = findViewById(R.id.btn_update);
        Button deleteButton = findViewById(R.id.btn_delete);
        Button cancelButton = findViewById(R.id.btn_cancel);

        textViewName.setVisibility(View.INVISIBLE);
        textViewHouseNo.setVisibility(View.INVISIBLE);
        textViewStreet.setVisibility(View.INVISIBLE);
        textViewTown.setVisibility(View.INVISIBLE);
        textViewPostcode.setVisibility(View.INVISIBLE);
        textViewPropertyType.setVisibility(View.INVISIBLE);
        textViewReceptions.setVisibility(View.INVISIBLE);
        textViewBathrooms.setVisibility(View.INVISIBLE);
        textViewBedrooms.setVisibility(View.INVISIBLE);

        editTextName.setVisibility(View.VISIBLE);
        editTextName.setFocusableInTouchMode(true);
        editTextHouseNo.setVisibility(View.VISIBLE);
        editTextHouseNo.setFocusableInTouchMode(true);
        editTextStreet.setVisibility(View.VISIBLE);
        editTextStreet.setFocusableInTouchMode(true);
        editTextTown.setVisibility(View.VISIBLE);
        editTextTown.setFocusableInTouchMode(true);
        editTextPostcode.setVisibility(View.VISIBLE);
        editTextPostcode.setFocusableInTouchMode(true);

        spinnerPropType.setVisibility(View.VISIBLE);
        spinnerNoReceps.setVisibility(View.VISIBLE);
        spinnerNoBaths.setVisibility(View.VISIBLE);
        spinnerNoBeds.setVisibility(View.VISIBLE);

        editButton.setVisibility(View.INVISIBLE);
        updateButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
    }

    private void cancelUpdate() {
        Toast toast = Toasty.info(UpdatePropertyActivity.this, "Update Cancelled", Toast.LENGTH_LONG, true);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 400);
        toast.show();

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.property_details_edit);

        Button editButton = findViewById(R.id.btn_edit);
        Button updateButton = findViewById(R.id.btn_update);
        Button cancelButton = findViewById(R.id.btn_cancel);
        Button deleteButton = findViewById(R.id.btn_delete);

        editTextName.setVisibility(View.INVISIBLE);
        editTextName.setFocusableInTouchMode(false);
        editTextHouseNo.setVisibility(View.INVISIBLE);
        editTextHouseNo.setFocusableInTouchMode(false);
        editTextStreet.setVisibility(View.INVISIBLE);
        editTextStreet.setFocusableInTouchMode(false);
        editTextTown.setVisibility(View.INVISIBLE);
        editTextTown.setFocusableInTouchMode(false);
        editTextPostcode.setVisibility(View.INVISIBLE);
        editTextPostcode.setFocusableInTouchMode(false);

        spinnerPropType.setVisibility(View.INVISIBLE);
        spinnerNoReceps.setVisibility(View.INVISIBLE);
        spinnerNoBaths.setVisibility(View.INVISIBLE);
        spinnerNoBeds.setVisibility(View.INVISIBLE);

        textViewName.setVisibility(View.VISIBLE);
        textViewHouseNo.setVisibility(View.VISIBLE);
        textViewStreet.setVisibility(View.VISIBLE);
        textViewTown.setVisibility(View.VISIBLE);
        textViewPostcode.setVisibility(View.VISIBLE);
        textViewPropertyType.setVisibility(View.VISIBLE);
        textViewReceptions.setVisibility(View.VISIBLE);
        textViewBathrooms.setVisibility(View.VISIBLE);
        textViewBedrooms.setVisibility(View.VISIBLE);

        editButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
    }

    private void deleteProperty() {
        final String dName = property.getCustomer();

        db.collection("Properties").document(property.getPropertyId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(UpdatePropertyActivity.this, dName + "'s Property has been deleted", Toast.LENGTH_LONG, true).show();
                            finish();
                            startActivity(new Intent(UpdatePropertyActivity.this, ExistingCustomersActivity.class));
                        } else {
                            Toasty.error(UpdatePropertyActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                            Log.d("Exception found ", String.valueOf(task.getException()));
                        }
                    }
                });
    }

    private void exCustomers() {
        Intent intent = new Intent(UpdatePropertyActivity.this, ExistingCustomersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void startSlideshow() {
        Intent intent = new Intent(UpdatePropertyActivity.this, PropertySlideshowActivity.class);
        String propId = propertyId;
        intent.putExtra("propertyId", propId);
        startActivity(intent);
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
                Intent homeIntent = new Intent(this, ExistingCustomersActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
