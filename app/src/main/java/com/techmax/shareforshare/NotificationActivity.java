package com.techmax.shareforshare;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.adapters.NoteRecyclerAdapter;
import com.techmax.shareforshare.model.Notemodel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_notification);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        toolbar=(Toolbar)findViewById(R.id.notification_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        empty=(LinearLayout)findViewById(R.id.empty);

        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        progressLeader=findViewById(R.id.progressLeader);

        mAuth= FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("notification").child(mAuth.getCurrentUser().getUid()).limitToLast(30);
        mRecyclerView =(RecyclerView)findViewById(R.id.noteList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // Update the RecyclerView item's list with menu items and Native Express ads.
        addNotificationItems();

        LinearLayoutManager lm=new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(lm);

        // Specify an adapter.
        adapter = new NoteRecyclerAdapter(this, mRecyclerViewItems);
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

                String p="";
                boolean f=true;
                long last=0;

                if(dataSnapshot.exists()) {
                    progressLeader.setVisibility(View.GONE);
                    empty.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRecyclerViewItems.clear();

                    for (DataSnapshot listItem : dataSnapshot.getChildren()) {
                        Notemodel lm = listItem.getValue(Notemodel.class);

                        if (lm != null) {
                            if (f) {
                                f = false;
                                p = getDate(lm.getTime());
                                last = lm.getTime();
                            }


                            if (!p.equals(getDate(lm.getTime()))) {
                                mRecyclerViewItems.add(last);
                                p = getDate(lm.getTime());
                                last = lm.getTime();

                            }
                            mRecyclerViewItems.add(lm);
                        }
                    }
                        mRecyclerViewItems.add(last);

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

    private String getDate(long milliSeconds) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(milliSeconds);
        return DateFormat.format("dd MMMM y", cal).toString();
    }


}
