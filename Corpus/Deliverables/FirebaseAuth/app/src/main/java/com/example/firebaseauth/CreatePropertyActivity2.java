package com.example.firebaseauth;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormatSymbols;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class CreatePropertyActivity2 extends AppCompatActivity implements View.OnClickListener {

    private Switch switchSR;
    private Switch switchParking;
    private Switch switchGarden;
    private Switch switchDoubleGlaze;
    private Switch switchGasHeat;
    private String propertyId;
    private Boolean saleRent;
    private Boolean forLet;
    private Boolean forSale;
    private Boolean parking;
    private Boolean garden;
    private Boolean doubleGlaze;
    private Boolean gasHeat;
    private TextView TextViewSaleRent;
    private TextView TextViewPark;
    private TextView TextViewGard;
    private TextView TextViewDoubGlaze;
    private TextView TextViewGas;
    private EditText EditTextPrice;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_property2);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        propertyId = Objects.requireNonNull(extras).getString("propertyId");

        TextViewSaleRent = findViewById(R.id.sale_rent_text);
        TextViewPark = findViewById(R.id.park_text);
        TextViewGard = findViewById(R.id.gard_text);
        TextViewDoubGlaze = findViewById(R.id.doub_glaze_text);
        TextViewGas = findViewById(R.id.gas_text);
        EditTextPrice = findViewById(R.id.price);

        forLet = false;
        forSale = false;
        saleRent = false;
        parking = false;
        garden = false;
        doubleGlaze = false;
        gasHeat = false;

        switchSR = findViewById(R.id.sale_rental);
        switchSR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    saleRent = true;
                    forLet = true;
                    forSale = false;
                    TextViewSaleRent.setText(getString(R.string.for_rent));
                    EditTextPrice.setHint("£ Monthly Rent");


                } else {
                    saleRent = false;
                    forLet = false;
                    forSale = true;
                    TextViewSaleRent.setText(getString(R.string.for_sale));
                    EditTextPrice.setHint("£ Sale Price");
                }
            }
        });

        switchParking = findViewById(R.id.parking);
        switchParking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    parking = true;
                    TextViewPark.setText(getString(R.string.yes));

                } else {
                    parking = false;
                    TextViewPark.setText(getString(R.string.no));
                }
            }
        });

        switchGarden = findViewById(R.id.garden);
        switchGarden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    garden = true;
                    TextViewGard.setText(getString(R.string.yes));

                } else {
                    garden = false;
                    TextViewGard.setText(getString(R.string.no));
                }
            }
        });

        switchDoubleGlaze = findViewById(R.id.doub_glaze);
        switchDoubleGlaze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    doubleGlaze = true;
                    TextViewDoubGlaze.setText(getString(R.string.yes));

                } else {
                    doubleGlaze = false;
                    TextViewDoubGlaze.setText(getString(R.string.no));
                }
            }
        });

        switchGasHeat = findViewById(R.id.gas);
        switchGasHeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    gasHeat = true;
                    TextViewGas.setText(getString(R.string.yes));

                } else {
                    gasHeat = false;
                    TextViewGas.setText(getString(R.string.no));
                }
            }
        });

        findViewById(R.id.btn_next2).setOnClickListener(this);
    }

    private void updateProp1() {

        progressBar.setVisibility(View.VISIBLE);

        String simpPrice = EditTextPrice.getText().toString().trim();
        simpPrice = simpPrice.replace(".",",");
        final String price;

        //if for sale or rent switch the string stored
        if(saleRent) {
            price = "£" + simpPrice + " pcm";
        } else {
            price = "£" + simpPrice;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference propertyRef = db.collection("Properties");


        propertyRef.whereEqualTo("propertyId", propertyId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        propertyRef.document(doc.getId()).update(
                                "parking", parking,
                                "garden", garden,
                                "dg", doubleGlaze,
                                "gasHeat", gasHeat,
                                "price", price,
                                "forLet", forLet,
                                "forSale", forSale
                        );
                        progressBar.setVisibility(View.GONE);
                        Toasty.success(CreatePropertyActivity2.this, "Property updated", Toast.LENGTH_LONG, true).show();
                        contCreate();
                    }
                } else {
                    Toasty.error(CreatePropertyActivity2.this, "Error check log", Toast.LENGTH_LONG, true).show();
                }
            }
        });
    }


    private void contCreate() {
        Intent intent = new Intent(CreatePropertyActivity2.this, CreatePropertyActivity3.class);
        String propId = propertyId;
        intent.putExtra("propertyId", propId);
        startActivity(intent);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next2:
                updateProp1();
                break;
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
