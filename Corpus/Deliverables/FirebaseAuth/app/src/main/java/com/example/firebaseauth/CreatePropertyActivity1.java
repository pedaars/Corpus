package com.example.firebaseauth;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

public class CreatePropertyActivity1 extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinnerPropType;
    private Spinner spinnerNoReceps;
    private Spinner spinnerNoBaths;
    private Spinner spinnerNoBeds;
    private EditText editTextHouseNo;
    private EditText editTextaddr;
    private TextView textViewSearchAddr;
    private String customerId;
    private String customerName;
    private String propertyId;
    private ProgressBar progressBar;
    String apiKey = BuildConfig.ApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_property1);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        customerId = Objects.requireNonNull(extras).getString("customerId");
        customerName = extras.getString("customerName");

        final String TAG = CreatePropertyActivity1.class.getSimpleName();

        TextView textViewName = findViewById(R.id.cust_name);
        spinnerPropType = findViewById(R.id.prop_type_spin);
        spinnerNoReceps = findViewById(R.id.no_receps_spin);
        spinnerNoBaths = findViewById(R.id.no_baths_spin);
        spinnerNoBeds = findViewById(R.id.no_beds_spin);
        editTextHouseNo = findViewById(R.id.prop_no);
        editTextaddr = findViewById(R.id.address);
        textViewSearchAddr = findViewById(R.id.addr_search);

        textViewName.setText(customerName);

        findViewById(R.id.btn_next2).setOnClickListener(this);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Objects.requireNonNull(autocompleteFragment).setHint("Search Postcode");
        autocompleteFragment.setCountry("GB");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //Log.i(TAG, "Place: " + place.getAddress() + ", " + place.getId());

                editTextaddr.setText(place.getAddress());
                textViewSearchAddr.setError(null);
                textViewSearchAddr.clearFocus();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private boolean validInputs(String propNo, String address, String propertyType, String receptions, String bathrooms, String bedrooms) {
        if (propNo.isEmpty()) {
            editTextHouseNo.setError("House Number required");
            editTextHouseNo.requestFocus();
            return true;
        }

        if (address.isEmpty()) {
            textViewSearchAddr.requestFocus();
            textViewSearchAddr.setError("Please Search Postcode");
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

    private void saveData() {

        String propNo = editTextHouseNo.getText().toString().trim().replace(",","");
        String addr = editTextaddr.getText().toString().trim().replace(",","");;
        String propertyType = spinnerPropType.getSelectedItem().toString().trim().replace(",","");;
        String receptions = spinnerNoReceps.getSelectedItem().toString().trim().replace(",","");;
        String bathrooms = spinnerNoBaths.getSelectedItem().toString().trim().replace(",","");;
        String bedrooms = spinnerNoBeds.getSelectedItem().toString().trim().replace(",","");;

        if (!validInputs(propNo, addr, propertyType, receptions, bathrooms, bedrooms)) {

            progressBar.setVisibility(View.VISIBLE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference newPropertyRef = db.collection("Properties").document();

            propertyId = newPropertyRef.getId();

            String creatorId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();

            String mainPhotoUrl = "";

            String[] addStrings = addr.split(" ");

            Address address = new Address();
            address.setHouseNo(propNo);
            address.setStreet(addStrings[0] + " " + addStrings[1]);
            address.setTown(addStrings[2]);
            address.setPostCode(addStrings[3] + " " + addStrings[4]);


            Property property = new Property(
                    customerName, bathrooms, bedrooms, receptions, propertyType,  address, creatorId,
                    customerId, propertyId, mainPhotoUrl
            );

            newPropertyRef.set(property).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        progressBar.setVisibility(View.GONE);
                        Toasty.success(CreatePropertyActivity1.this, "New Property Created", Toast.LENGTH_LONG, true).show();
                        contCreate();

                    } else {
                        Toasty.error(CreatePropertyActivity1.this, "Error check log", Toast.LENGTH_LONG, true).show();
                    }
                }
            });
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next2:
                saveData();
                break;
        }
    }

    private void contCreate() {
        Intent intent = new Intent(CreatePropertyActivity1.this, CreatePropertyActivity2.class);
        String propId = propertyId;
        intent.putExtra("propertyId", propId);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
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
