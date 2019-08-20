package com.techmax.shareforshare;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.helper.BitcoinAddressValidator;
import com.techmax.shareforshare.model.EarningProfile;
import com.github.guilhe.circularprogressview.CircularProgressView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class BalanceActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private static final float thre=0.01f;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, eDatabase;
    private ValueEventListener earningListener;

    private LinearLayout wallet_wrapper;
    private TextView ecr, claimed, referral,lifetime,epcr;
    private CircularProgressView ep;
    private EditText wallet;
    private Button preq;

    private Snackbar snackbar;

    private IntentFilter filter;

    private ConnectivityReceiver receiver;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);


        Toolbar toolbar = findViewById(R.id.toolbar_balance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Earning Profile");
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

        AdView mAdView = findViewById(R.id.adView);
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

        mAuth= FirebaseAuth.getInstance();
        receiver=new ConnectivityReceiver();
        filter=new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        snackbar = Snackbar.make(findViewById(R.id.root), R.string.internet_msg, Snackbar.LENGTH_INDEFINITE);


        wallet_wrapper=findViewById(R.id.wallet_wrapper);
        ecr =  findViewById(R.id.current_balance);
        claimed =  findViewById(R.id.claimed_balance);
        referral = findViewById(R.id.referral_balance);
        lifetime =  findViewById(R.id.lifetime);
        epcr =  findViewById(R.id.earning_percent);
        wallet= findViewById(R.id.wallet_address);
        preq=findViewById(R.id.req_button);
        ep= findViewById(R.id.earning_progress);
        ep.setProgress(0);

        checkConnection();

    }

    @Override
    public void onBackPressed() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else{
            super.onBackPressed();
        }
    }

    private void makeTransaction(final String adr) {

        showProgressDialog();
        setProgressMessage("Generating Request...");

        FirebaseUser user=mAuth.getCurrentUser();
        // Write new user
        final String uid=user.getUid();

        mDatabase.child("earningprofile").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    EarningProfile ep=dataSnapshot.getValue(EarningProfile.class);

                    if((ep != null ? ep.getEcr() : 0) >=thre) {

                        HashMap<String, Object> payoutMap = new HashMap<>();
                        payoutMap.put("pw", adr);
                        payoutMap.put("pa", ep.getEcr());
                        payoutMap.put("pc", ep.getEc());
                        payoutMap.put("pr", ep.getEr());
                        payoutMap.put("pclk", ep.getEclk());
                        payoutMap.put("pt", ServerValue.TIMESTAMP);
                        payoutMap.put("ps", 0);
                        payoutMap.put("psm", "");

                        final String key = mDatabase.child("payouts").child(uid).push().getKey();

                        Map requestMap=new HashMap();
                        requestMap.put("payouts/"+uid+"/"+key,payoutMap);

                        ep.setEcr(0);
                        ep.setEc(0);
                        ep.setEr(0);
                        ep.setEclk(0);
                        ep.setEw(adr);

                        requestMap.put("earningprofile/"+uid,ep);



                        mDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                hideProgressDialog();
                                if(databaseError!=null) {
                                    Toast.makeText(BalanceActivity.this, "Error Occured" + databaseError, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent i=new Intent(BalanceActivity.this, TransactionActivity.class);
                                    i.putExtra("txn_id",key);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        });
                    }
                    else{
                        hideProgressDialog();
                        Toast.makeText(BalanceActivity.this, "Invalid Amount", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    hideProgressDialog();
                    Toast.makeText(BalanceActivity.this, "Database Connection Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                Toast.makeText(BalanceActivity.this, "Error" +databaseError, Toast.LENGTH_SHORT).show();

            }
        });






    }





    @Override
    public void onStart() {
        super.onStart();


        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {

            startActivity(new Intent(BalanceActivity.this, AuthActivity.class));
            finish();


        } else {

            mDatabase = FirebaseDatabase.getInstance().getReference();
            eDatabase = FirebaseDatabase.getInstance().getReference().child("earningprofile").child(mAuth.getCurrentUser().getUid());

            earningListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double e=Double.parseDouble(Objects.toString(dataSnapshot.child("ecr").getValue(),null));
                    if(e>=thre){
                        wallet_wrapper.setVisibility(View.VISIBLE);
                        String w=Objects.toString(dataSnapshot.child("ew").getValue(),null);
                        if(!w.isEmpty()){
                            wallet.setText(w);
                        }
                    }
                    else
                        wallet_wrapper.setVisibility(View.GONE);
                    animateTextView((float) Double.parseDouble(ecr.getText().toString()), (float) e, ecr);
                    //ecr.setText(String.format("%.8f",(float)e));
                    animateTextView((float) Double.parseDouble(claimed.getText().toString()), (float) Double.parseDouble(Objects.toString(dataSnapshot.child("ec").getValue(),"0")), claimed);
                    //claimed.setText(String.format("%.8f",(float) Double.parseDouble(dataSnapshot.child("ec").getValue().toString())));
                    animateTextView((float) Double.parseDouble(referral.getText().toString()), (float) Double.parseDouble(Objects.toString(dataSnapshot.child("er").getValue(),"0")), referral);
                    //referral.setText(String.format("%.8f",(float) Double.parseDouble(dataSnapshot.child("er").getValue().toString())));
                    animateTextView((float) Double.parseDouble(lifetime.getText().toString()), (float) Double.parseDouble(Objects.toString(dataSnapshot.child("et").getValue(),"0")), lifetime);
                    //lifetime.setText(String.format("%.8f",(float) Double.parseDouble(dataSnapshot.child("et").getValue().toString())));

                    int p= (int) (e*100/thre);
                    ep.setProgress((p>100?100:p));

                    epcr.setText((p>100?100:p)+"%");


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            eDatabase.addValueEventListener(earningListener);


        }

    }

    public void animateTextView(float initialValue, float finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(initialValue, finalValue);
        valueAnimator.setDuration(1500);
        valueAnimator.setInterpolator(new DecelerateInterpolator(0.8f));

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(String.format(Locale.US,"%.8f", (float)valueAnimator.getAnimatedValue()));

            }
        });
        valueAnimator.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        ShareApplication.getInstance().setConnectivityListener(this);
        registerReceiver(receiver,filter);
        wallet.clearFocus();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        return isConnected;
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {


        if (!isConnected) {

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
            if(preq!=null&& preq.isShown())
                preq.setEnabled(false);
        } else {
            if(preq!=null)
                preq.setEnabled(true);
            if (snackbar.isShownOrQueued())
                snackbar.dismiss();

        }
    }


    @Override
    protected void onDestroy() {

        if (earningListener != null) {
            eDatabase.removeEventListener(earningListener);
        }
        super.onDestroy();
    }


}
