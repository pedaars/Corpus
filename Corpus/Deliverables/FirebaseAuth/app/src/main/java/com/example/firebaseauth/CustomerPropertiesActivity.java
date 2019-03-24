package com.example.firebaseauth;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerPropertiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PropertiesAdapter adapter;
    private List<Property> propertyList;
    private ProgressBar progressBar;
    private String customerId;
    private String customerName;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_properties);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        customerId = Objects.requireNonNull(extras).getString("customerId");
        customerName = extras.getString("customerName");

        progressBar = findViewById(R.id.progressbar);

        FloatingActionButton addProp = findViewById(R.id.floating_add_prop);
        addProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerPropertiesActivity.this, CreatePropertyActivity1.class);
                String custName = customerName;
                String custId = customerId;
                intent.putExtra("customerName", custName);
                intent.putExtra("customerId", custId);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerview_properties);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        propertyList = new ArrayList<>();
        adapter = new PropertiesAdapter(this, propertyList);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("Properties")
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressBar.setVisibility(View.GONE);

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                Property p = d.toObject(Property.class);
                                Objects.requireNonNull(p).setPropertyId(d.getId());
                                propertyList.add(p);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
