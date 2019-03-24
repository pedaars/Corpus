package com.example.firebaseauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PropertySlideshowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SlideshowAdapter adapter;
    private List<Room> roomList;
    private String propertyId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_slideshow);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        propertyId = Objects.requireNonNull(extras).getString("propertyId");

        recyclerView = findViewById(R.id.recyclerview_slideshow);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        adapter = new SlideshowAdapter(this, roomList);

        recyclerView.setAdapter(adapter);

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference propertiesRef = rootRef.collection("Properties");

            propertiesRef.whereEqualTo("propertyId", propertyId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if (task1.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task1.getResult())) {
                                propertiesRef.document(document.getId()).collection("Rooms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        progressBar.setVisibility(View.GONE);

                                        if (!queryDocumentSnapshots.isEmpty()) {

                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                            Log.d("DONE", list.toString());

                                            for (DocumentSnapshot d : list) {

                                                Room room = d.toObject(Room.class);
                                                roomList.add(room);
                                                Log.d("ROOM", String.valueOf(roomList.get(0)));
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
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
