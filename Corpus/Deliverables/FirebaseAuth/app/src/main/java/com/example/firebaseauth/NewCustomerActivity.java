package com.example.firebaseauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

public class NewCustomerActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = NewCustomerActivity.class.getSimpleName();
    private EditText editTextName;
    private EditText editTextNumber;
    private EditText editTextEmail;
    private EditText editTextHouseNo;
    private EditText editTextaddr;
    private TextView textViewSearchAddr;
    String apiKey = BuildConfig.ApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextName = findViewById(R.id.name);
        editTextNumber = findViewById(R.id.phone_no);InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(11);
        editTextNumber.setFilters(filterArray);
        editTextEmail = findViewById(R.id.email);
        editTextHouseNo = findViewById(R.id.prop_no);
        editTextaddr = findViewById(R.id.address);
        textViewSearchAddr = findViewById(R.id.addr_search);

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

    private boolean validInputs(String name, String phoneNo, String email, String propNo, String address) {
        if (name.isEmpty()) {
            editTextName.setError("Name required");
            editTextName.requestFocus();
            return true;
        }

        if (phoneNo.isEmpty()) {
            editTextNumber.setError("Phone Number required");
            editTextNumber.requestFocus();
            return true;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email required");
            editTextEmail.requestFocus();
            return true;
        }

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
        return false;
    }

    private void saveData() {

        String name = editTextName.getText().toString().trim().replace(",","");;
        String phoneNo = editTextNumber.getText().toString().trim().replace(",","");;
        String email = editTextEmail.getText().toString().trim().replace(",","");;
        String propNo = editTextHouseNo.getText().toString().trim().replace(",","");;
        String addr = editTextaddr.getText().toString().trim().replace(",","");;

        if (!validInputs(name, phoneNo, email, propNo, addr)) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference newCustomerRef = db.collection("Customers").document();

            String creatorId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
            String customerId = newCustomerRef.getId();

            String[] addStrings = addr.split(" ");

            Address address = new Address();
                    address.setHouseNo(propNo);
                    address.setStreet(addStrings[0] + " " + addStrings[1]);
                    address.setTown(addStrings[2]);
                    address.setPostCode(addStrings[3] + " " + addStrings[4]);


            Customer customer = new Customer(
                    name, email, phoneNo, address, creatorId,
                    customerId
            );

            newCustomerRef.set(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toasty.success(NewCustomerActivity.this, "Customer Added", Toast.LENGTH_LONG, true).show();
                                exCustomer();

                            } else {
                                Toasty.error(NewCustomerActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
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

    private void exCustomer() {
        Intent intent = new Intent(NewCustomerActivity.this, ExistingCustomersActivity.class);
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
                Intent homeIntent = new Intent(this, ExistingCustomersActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
