package com.techmax.shareforshare;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.adapters.LeaderRecyclerAdapter;
import com.techmax.shareforshare.model.EarningProfile;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private Query mDatabase;
    private ValueEventListener mListener;

    private FirebaseAuth mAuth;

    private List<Object> mRecyclerViewItems = new ArrayList<>();



    private ProgressBar progressLeader;

    private SwipeRefreshLayout mySwipeRefreshLayout;


    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });

        toolbar=findViewById(R.id.leaderboard_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top 100");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    finish();
                }
            }
        });



        mAuth= FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("earningprofile").orderByChild("et").limitToLast(100);


        mRecyclerView =findViewById(R.id.userList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // Update the RecyclerView item's list with menu items and Native Express ads.





        LinearLayoutManager lm=new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new LeaderRecyclerAdapter(this, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        addLeaderBoardItems();





        progressLeader=findViewById(R.id.progressLeader);
        mySwipeRefreshLayout=findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                            addLeaderBoardItems();

                    }
                }
        );
    }


    @Override
    public void onBackPressed() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else{
            super.onBackPressed();
        }
    }




    private void addLeaderBoardItems() {
        mListener= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    progressLeader.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);

                    mRecyclerViewItems.clear();
                    for (DataSnapshot listItem : dataSnapshot.getChildren()) {
                        EarningProfile lm = listItem.getValue(EarningProfile.class);


                        if (lm != null) {
                            lm.setKey(listItem.getKey());
                        }

                        mRecyclerViewItems.add(lm);

                    }

                    adapter.notifyDataSetChanged();
                }
                else
                {
                    progressLeader.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressLeader.setVisibility(View.GONE);
                mySwipeRefreshLayout.setRefreshing(false);

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
