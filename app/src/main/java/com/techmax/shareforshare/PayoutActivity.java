package com.techmax.shareforshare;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.adapters.PayoutRecyclerAdapter;
import com.techmax.shareforshare.model.Payout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PayoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private Query mDatabase;
    private LinearLayout empty;
    private ValueEventListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseAuth mAuth;
    private ProgressBar progressLeader;


    // List of Native Express ads and MenuItems that populate the RecyclerView.
    private List<Object> mRecyclerViewItems = new ArrayList<>();

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        toolbar=(Toolbar)findViewById(R.id.payout_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payout History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        empty=(LinearLayout)findViewById(R.id.empty);

        mAuth= FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("payouts").child(mAuth.getCurrentUser().getUid());

        mRecyclerView =(RecyclerView)findViewById(R.id.txnList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        progressLeader=findViewById(R.id.progressLeader);

        // Update the RecyclerView item's list with menu items and Native Express ads.
        addNotificationItems();

        LinearLayoutManager lm=new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(lm);

        // Specify an adapter.
        adapter = new PayoutRecyclerAdapter(this, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                        addNotificationItems();

                    }
                }
        );
    }

    private void addNotificationItems() {

        mListener= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    progressLeader.setVisibility(View.GONE);
                    empty.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRecyclerViewItems.clear();
                    for (DataSnapshot listItem : dataSnapshot.getChildren()) {
                        Payout lm = listItem.getValue(Payout.class);

                        lm.setKey(listItem.getKey());

                        mRecyclerViewItems.add(lm);

                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    progressLeader.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressLeader.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        };

        mDatabase.addListenerForSingleValueEvent(mListener);
    }

    @Override
    protected void onDestroy() {

        if (mListener != null) {
            mDatabase.removeEventListener(mListener);
        }
        super.onDestroy();
    }




}
