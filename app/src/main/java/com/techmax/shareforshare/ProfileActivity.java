package com.techmax.shareforshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.adapters.DeviceRecyclerAdapter;
import com.techmax.shareforshare.helper.DividerItemDecoration;
import com.techmax.shareforshare.helper.TimeUtils;
import com.techmax.shareforshare.model.UserProfiles;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private Query mDatabase,uDatabase;
    private ValueEventListener mListener,uListener;

    private TimeUtils timeUtils=new TimeUtils();

    private Toolbar toolbar;

    private TextView profile_name,profile_email,ac_status,ac_created,ac_signed,md;

    private CircleImageView ci;

    private List<UserProfiles> mRecyclerViewItems = new ArrayList<>();

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Account");
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


        mDatabase= FirebaseDatabase.getInstance().getReference().child("userprofiles").child("");;
        uDatabase= FirebaseDatabase.getInstance().getReference().child("users").child("");;

        profile_name=findViewById(R.id.profile_name);
        profile_email=findViewById(R.id.profile_email);
        ac_status=(TextView)findViewById(R.id.ac_status);
        ac_created=(TextView)findViewById(R.id.ac_created);
        ac_signed=(TextView)findViewById(R.id.ac_signed);
        ci=(CircleImageView)findViewById(R.id.profile_image);
        md=(TextView)findViewById(R.id.md);

        mRecyclerView =(RecyclerView)findViewById(R.id.device_list);
        mRecyclerView.setHasFixedSize(true);

        // Update the RecyclerView item's list with menu items and Native Express ads.
        addDeviceItems();
        //loadUserData();

        LinearLayoutManager lm=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Specify an adapter.
        adapter = new DeviceRecyclerAdapter(this, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

    }

    private void addDeviceItems() {
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mRecyclerViewItems.clear();
                    for (DataSnapshot listItem : dataSnapshot.getChildren()) {
                        UserProfiles up = listItem.getValue(UserProfiles.class);


                        mRecyclerViewItems.add(up);

                    }
                    md.setText("My Devices ("+mRecyclerViewItems.size()+")");
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    private void loadUserData(){

        FirebaseUser u=mAuth.getCurrentUser();

        profile_name.setText(u.getDisplayName());
        profile_email.setText(u.getEmail());

        Picasso.with(this).load(u.getPhotoUrl()).into(ci);

        mDatabase.addValueEventListener(mListener);

        uListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    int s= Integer.parseInt(Objects.toString(dataSnapshot.child("us").getValue(),"1"));
                    switch (s){
                        case 0:
                            ac_status.setTextColor(getResources().getColor(R.color.colorYellow));
                            ac_status.setText(R.string.suspended);
                            ac_status.setBackgroundResource(R.drawable.suspend_bg);
                            break;
                        case 1:
                            ac_status.setText(R.string.active);
                            ac_status.setBackgroundResource(R.drawable.active_bg);
                            break;
                        default:
                            ac_status.setText(R.string.active);
                            ac_status.setBackgroundResource(R.drawable.active_bg);
                            break;
                    }

                    long c= Long.parseLong(Objects.toString(dataSnapshot.child("uc").getValue(),"0"));
                    long l= Long.parseLong(Objects.toString(dataSnapshot.child("ul").getValue(),"0"));

                    ac_created.setText(timeUtils.createDate(c,"d MMM yyyy"));
                    ac_signed.setText(DateUtils.getRelativeTimeSpanString(l,new Date().getTime(),DateUtils.MINUTE_IN_MILLIS));




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        uDatabase.addListenerForSingleValueEvent(uListener);
    }

    @Override
    protected void onDestroy() {

        if (mListener != null) {
            mDatabase.removeEventListener(mListener);
        }
        if (uListener != null) {
            uDatabase.removeEventListener(uListener);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else{
            super.onBackPressed();
        }
    }
}
