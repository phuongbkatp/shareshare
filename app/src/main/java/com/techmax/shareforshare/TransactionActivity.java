package com.techmax.shareforshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techmax.shareforshare.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ValueEventListener txnListener;

    private Toolbar toolbar;

    private String key,txnLink="";

    private TextView created,amount,status,tc,tr,tw,errorText;

    private ImageView stbg, sticon;

    private Button successBtn;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        key=getIntent().getStringExtra("txn_id");
        toolbar = (Toolbar) findViewById(R.id.toolbar_transaction);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaction Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAuth= FirebaseAuth.getInstance();

        created=(TextView)findViewById(R.id.created);
        amount=(TextView)findViewById(R.id.amount);
        status=(TextView)findViewById(R.id.status);
        tc=(TextView)findViewById(R.id.txnClaimed);
        tr=(TextView)findViewById(R.id.txnReferral);
        tw=(TextView)findViewById(R.id.txnWallet);
        errorText=(TextView)findViewById(R.id.errorText);
        stbg=(ImageView)findViewById(R.id.stbg);
        sticon=(ImageView)findViewById(R.id.sticon);
        successBtn=(Button)findViewById(R.id.successBtn);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("payouts").child(mAuth.getCurrentUser().getUid()).child(key);

        loadTxnData(key);



    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {

            startActivity(new Intent(TransactionActivity.this, AuthActivity.class));
            finish();
        }
    }

    private void loadTxnData(String key) {

        txnListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int s= Integer.parseInt(dataSnapshot.child("ps").getValue().toString());
                switch (s){
                    case -1:
                        if(successBtn.isShown())successBtn.setVisibility(View.GONE);
                        sticon.setImageResource(R.drawable.ic_warning);
                        status.setText("Failed");
                        errorText.setText(dataSnapshot.child("psm").getValue().toString());
                        errorText.setVisibility(View.VISIBLE);
                        break;
                    case 0:
                        sticon.setImageResource(R.drawable.ic_pending);
                        status.setText("Pending");
                        if(errorText.isShown())errorText.setVisibility(View.GONE);
                        if(successBtn.isShown())successBtn.setVisibility(View.GONE);

                        break;
                    case 1:
                        sticon.setImageResource(R.drawable.ic_check);
                        status.setText("Success");
                        if(errorText.isShown())errorText.setVisibility(View.GONE);
                        txnLink=dataSnapshot.child("psm").getValue().toString();
                        successBtn.setVisibility(View.VISIBLE);


                }

                created.setText(createDate(Long.parseLong(dataSnapshot.child("pt").getValue().toString())));
                amount.setText(String.format(Locale.US,"%.5f BTC",Double.parseDouble(dataSnapshot.child("pa").getValue().toString())));
                tc.setText(String.format(Locale.US,"%.5f",Double.parseDouble(dataSnapshot.child("pc").getValue().toString())));
                tr.setText(String.format(Locale.US,"%.5f",Double.parseDouble(dataSnapshot.child("pr").getValue().toString())));
                tw.setText(dataSnapshot.child("pw").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase.addValueEventListener(txnListener);

        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                Uri uri = Uri.parse(txnLink);
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setShowTitle(true);
                intentBuilder.setToolbarColor(ContextCompat.getColor(TransactionActivity.this, R.color.colorPrimary));
                intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(TransactionActivity.this, R.color.colorPrimaryDark));
                CustomTabsIntent customTabsIntent = intentBuilder.build();

                customTabsIntent.launchUrl(TransactionActivity.this, uri);
            }catch (Exception e){
                Toast.makeText(TransactionActivity.this, "Invalid Transaction Link", Toast.LENGTH_SHORT).show();

            }


            }
        });


    }

    @Override
    protected void onDestroy() {

        if (txnListener != null) {
            mDatabase.removeEventListener(txnListener);
        }
        super.onDestroy();
    }


    public CharSequence createDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a, d MMM ''yy");
        return sdf.format(d);
    }
}
