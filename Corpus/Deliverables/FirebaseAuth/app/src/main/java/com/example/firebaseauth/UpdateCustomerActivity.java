package com.example.firebaseauth;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

public class UpdateCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName;
    private EditText editTextNumber;
    private EditText editTextEmail;
    private EditText editTextHouseNo;
    private EditText editTextStreet;
    private EditText editTextTown;
    private EditText editTextPostcode;

    private TextView textViewName;
    private TextView textViewNumber;
    private TextView textViewEmail;
    private TextView textViewHouseNo;
    private TextView textViewStreet;
    private TextView textViewTown;
    private TextView textViewPostcode;

    private String customerId;
    private String customerName;

    private String creatorId;

    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);

        customer = (Customer) getIntent().getSerializableExtra("customer");

        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.name);
        editTextNumber = findViewById(R.id.phone_no);
        editTextEmail = findViewById(R.id.email);
        editTextHouseNo = findViewById(R.id.prop_no);
        editTextStreet = findViewById(R.id.street_name);
        editTextTown = findViewById(R.id.town);
        editTextPostcode = findViewById(R.id.postcode);

        textViewName = findViewById(R.id.name_v);
        textViewNumber = findViewById(R.id.phone_no_v);
        textViewEmail = findViewById(R.id.email_v);
        textViewHouseNo = findViewById(R.id.prop_no_v);
        textViewStreet = findViewById(R.id.street_name_v);
        textViewTown = findViewById(R.id.town_v);
        textViewPostcode = findViewById(R.id.postcode_v);

        editTextName.setText(customer.getName());
        editTextNumber.setText(customer.getPhoneNo());
        editTextEmail.setText(customer.getEmail());
        editTextHouseNo.setText(customer.getAddress().getHouseNo());
        editTextStreet.setText(customer.getAddress().getStreet());
        editTextTown.setText(customer.getAddress().getTown());
        editTextPostcode.setText(customer.getAddress().getPostCode());

        textViewName.setText(customer.getName());
        textViewNumber.setText(customer.getPhoneNo());
        textViewEmail.setText(customer.getEmail());
        textViewHouseNo.setText(customer.getAddress().getHouseNo());
        textViewStreet.setText(customer.getAddress().getStreet());
        textViewTown.setText(customer.getAddress().getTown());
        textViewPostcode.setText(customer.getAddress().getPostCode());

        @SuppressLint("CutPasteId") TextView custNameTxt = findViewById(R.id.name_v);
        customerName = custNameTxt.getText().toString().trim();
        customerId = customer.getCustomerId();
        Log.d("CUSTOMERID ", customerId);

        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_cust_prop).setOnClickListener(this);
        findViewById(R.id.btn_add_prop).setOnClickListener(this);

    }

    private boolean validInputs(String name, String phoneNo, String email, String propNo, String streetName, String town, String postCode) {
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
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
            editTextPostcode.setError("Address required");
            editTextPostcode.requestFocus();
            return true;
        }
        return false;
    }

    private void updateCustomer() {

        final String name = editTextName.getText().toString().trim().replace(",","");;
        String phoneNo = editTextNumber.getText().toString().trim().replace(",","");;
        String email = editTextEmail.getText().toString().trim().replace(",","");;
        String propNo = editTextHouseNo.getText().toString().trim().replace(",","");;
        String streetName = editTextStreet.getText().toString().trim().replace(",","");;
        String town = editTextTown.getText().toString().trim().replace(",","");;
        String postCode = editTextPostcode.getText().toString().trim().replace(",","");;

        if (!validInputs(name, phoneNo, email, propNo, streetName, town, postCode)) {

            progressBar.setVisibility(View.VISIBLE);

            creatorId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();

            Address address = new Address();
            address.setHouseNo(propNo);
            address.setStreet(streetName);
            address.setTown(town);
            address.setPostCode(postCode);

            Customer c = new Customer(
                    name, email, phoneNo, address, creatorId, customerId
            );

            db.collection("Customers").document(customer.getCustomerId())
                    .update(
                            "name", c.getName(),
                            "email", c.getEmail(),
                            "phoneNo", c.getPhoneNo(),
                            "address", c.getAddress(),
                            "creatorId", c.getCreatorId()
                    )
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                //Toast.makeText(UpdateCustomerActivity.this, "Customer Updated", Toast.LENGTH_SHORT).show();

                                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                final CollectionReference propertiesRef = rootRef.collection("Properties");
                                final CollectionReference customersRef = rootRef.collection("Customers");
                                customersRef.whereEqualTo("customerId", customerId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                        if (task2.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task2.getResult())) {
                                                final String newName = document.getString("name");
                                                propertiesRef.whereEqualTo("customerId", customerId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task3) {
                                                        if (task3.isSuccessful()) {
                                                            if(Objects.requireNonNull(task3.getResult()).isEmpty()) {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toasty.success(UpdateCustomerActivity.this, newName + "'s Details have been Updated", Toast.LENGTH_LONG, true).show();
                                                                exCustomer();
                                                            } else {
                                                                for (QueryDocumentSnapshot doc : Objects.requireNonNull(task3.getResult())) {
                                                                    propertiesRef.document(doc.getId()).update("customer", newName);
                                                                    progressBar.setVisibility(View.GONE);
                                                                    Toasty.success(UpdateCustomerActivity.this, newName + "'s Details and Properties have been Updated", Toast.LENGTH_LONG, true).show();
                                                                    exCustomer();
                                                                }
                                                            }

                                                        } else {
                                                            Toasty.error(UpdateCustomerActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                                            Log.d("T3 Exception found ", String.valueOf(task3.getException()));
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            Toasty.error(UpdateCustomerActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                            Log.d("T2 Exception found ", String.valueOf(task2.getException()));
                                        }
                                    }
                                });

                            } else {
                                Toasty.error(UpdateCustomerActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                Log.d("T1 Exception found ", String.valueOf(task1.getException()));
                            }
                        }
                    });
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                updateCustomer();
                break;
            case R.id.btn_edit:
                focusEdit();
                break;
            case R.id.btn_cancel:
                cancelUpdate();
                break;
            case R.id.btn_add_prop:
                addProperty();
                break;
            case R.id.btn_cust_prop:
                custProperties();
                break;
            case R.id.btn_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleting a Customer is permanent...");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCustomer();
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
        Toast toast = Toasty.info(UpdateCustomerActivity.this, "Update Customer details and click update to save", Toast.LENGTH_LONG, true);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 400);
        toast.show();

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.update_customer_details);

        Button editButton = findViewById(R.id.btn_edit);
        Button updateButton = findViewById(R.id.btn_update);
        Button cancelButton = findViewById(R.id.btn_cancel);
        Button deleteButton = findViewById(R.id.btn_delete);

        textViewName.setVisibility(View.INVISIBLE);
        textViewNumber.setVisibility(View.INVISIBLE);
        textViewEmail.setVisibility(View.INVISIBLE);
        textViewHouseNo.setVisibility(View.INVISIBLE);
        textViewStreet.setVisibility(View.INVISIBLE);
        textViewTown.setVisibility(View.INVISIBLE);
        textViewPostcode.setVisibility(View.INVISIBLE);

        editTextName.setVisibility(View.VISIBLE);
        editTextName.setFocusableInTouchMode(true);
        editTextNumber.setVisibility(View.VISIBLE);
        editTextNumber.setFocusableInTouchMode(true);
        editTextEmail.setVisibility(View.VISIBLE);
        editTextEmail.setFocusableInTouchMode(true);
        editTextHouseNo.setVisibility(View.VISIBLE);
        editTextHouseNo.setFocusableInTouchMode(true);
        editTextStreet.setVisibility(View.VISIBLE);
        editTextStreet.setFocusableInTouchMode(true);
        editTextTown.setVisibility(View.VISIBLE);
        editTextTown.setFocusableInTouchMode(true);
        editTextPostcode.setVisibility(View.VISIBLE);
        editTextPostcode.setFocusableInTouchMode(true);

        editButton.setVisibility(View.INVISIBLE);
        updateButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
    }

    private void cancelUpdate() {
        Toast toast = Toasty.info(UpdateCustomerActivity.this, "Update Cancelled", Toast.LENGTH_LONG, true);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 400);
        toast.show();

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.customer_details);

        Button editButton = findViewById(R.id.btn_edit);
        Button updateButton = findViewById(R.id.btn_update);
        Button cancelButton = findViewById(R.id.btn_cancel);
        Button deleteButton = findViewById(R.id.btn_delete);

        editTextName.setVisibility(View.INVISIBLE);
        editTextName.setFocusableInTouchMode(true);
        editTextNumber.setVisibility(View.INVISIBLE);
        editTextNumber.setFocusableInTouchMode(true);
        editTextEmail.setVisibility(View.INVISIBLE);
        editTextEmail.setFocusableInTouchMode(true);
        editTextHouseNo.setVisibility(View.INVISIBLE);
        editTextHouseNo.setFocusableInTouchMode(true);
        editTextStreet.setVisibility(View.INVISIBLE);
        editTextStreet.setFocusableInTouchMode(true);
        editTextTown.setVisibility(View.INVISIBLE);
        editTextTown.setFocusableInTouchMode(true);
        editTextPostcode.setVisibility(View.INVISIBLE);
        editTextPostcode.setFocusableInTouchMode(true);

        textViewName.setVisibility(View.VISIBLE);
        textViewNumber.setVisibility(View.VISIBLE);
        textViewEmail.setVisibility(View.VISIBLE);
        textViewHouseNo.setVisibility(View.VISIBLE);
        textViewStreet.setVisibility(View.VISIBLE);
        textViewTown.setVisibility(View.VISIBLE);
        textViewPostcode.setVisibility(View.VISIBLE);

        editButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
    }

    private void deleteCustomer() {
        final String dName = customer.getName();

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference propertiesRef = rootRef.collection("Properties");
        final CollectionReference customersRef = rootRef.collection("Customers");

        propertiesRef.whereEqualTo("customerId", customerId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                if (task2.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task2.getResult())) {
                        propertiesRef.document(document.getId()).delete();

                        customersRef.document(customer.getCustomerId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(UpdateCustomerActivity.this, dName + " Customer details and any properties have been deleted", Toast.LENGTH_LONG, true).show();
                                    finish();
                                    startActivity(new Intent(UpdateCustomerActivity.this, ExistingCustomersActivity.class));
                                } else {
                                    Toasty.error(UpdateCustomerActivity.this, "Error check log", Toast.LENGTH_LONG, true).show();
                                    Log.d("Exception found ", String.valueOf(task.getException()));
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    private void exCustomer() {
        Intent intent = new Intent(UpdateCustomerActivity.this, ExistingCustomersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void addProperty() {
        Intent intent = new Intent(UpdateCustomerActivity.this, CreatePropertyActivity1.class);
        String custName = customerName;
        String custId = customerId;
        intent.putExtra("customerName", custName);
        intent.putExtra("customerId", custId);
        startActivity(intent);
    }

    private void custProperties() {
        Intent intent = new Intent(UpdateCustomerActivity.this, CustomerPropertiesActivity.class);
        String custName = customerName;
        String custId = customerId;
        intent.putExtra("customerName", custName);
        intent.putExtra("customerId", custId);
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
