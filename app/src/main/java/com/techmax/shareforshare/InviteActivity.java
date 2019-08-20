package com.techmax.shareforshare;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.adapters.ReferralRecyclerAdapter;
import com.techmax.shareforshare.model.Users;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InviteActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private Query mDatabase;
    private DatabaseReference iDatabase,cDatabase;
    private ValueEventListener mListener,iListener,cListener;

    private Toolbar toolbar;
    private TextView rc,iTitle,iBody,refresh,more;
    private ImageView iv;
    private LinearLayout bd,empty;
    private ImageButton rfs;
    private Button copyBtn, inviteBtn;
    private List<Users> mRecyclerViewItems = new ArrayList<>();
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

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

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invite & Earn");
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





        String uid=mAuth.getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").orderByChild("ur").equalTo(uid).limitToLast(18);
        cDatabase= FirebaseDatabase.getInstance().getReference().child("referral").child(uid).child("ref_list");
        iDatabase= FirebaseDatabase.getInstance().getReference().child("refoffer");
        rc=findViewById(R.id.rct);
        iTitle=findViewById(R.id.iTitle);
        iBody=findViewById(R.id.iBody);
        rfs=findViewById(R.id.ref_refresh);
        refresh=findViewById(R.id.refresh);
        copyBtn=findViewById(R.id.copyBtn);
        inviteBtn=findViewById(R.id.inviteBtn);
        more=findViewById(R.id.more);
        iv=findViewById(R.id.invite_img);
        bd=findViewById(R.id.bd);
        empty=findViewById(R.id.empty);

        mRecyclerView =(RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        addReferralBoardItems();
        GridLayoutManager lm=new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(lm);

        adapter = new ReferralRecyclerAdapter(this, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        rfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfs.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                rfs.setEnabled(false);
                mRecyclerViewItems.clear();
                addReferralBoardItems();
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label="Referral Link";
                String text="https://play.google.com/store/apps/details?id="+getPackageName()+"&referrer="+mAuth.getCurrentUser().getUid();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(label, text);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(InviteActivity.this, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show();

            }
        });

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    String sAux = getString(R.string.invite_cap);
                    sAux = sAux + "https://play.google.com/store/apps/details?id="+getPackageName()+"&referrer="+mAuth.getCurrentUser().getUid();
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));

                } catch(Exception e) {
                }

            }
        });




    }

    private void addReferralBoardItems() {

        mListener= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot listItem : dataSnapshot.getChildren()) {
                            Users ru = listItem.getValue(Users.class);
                            mRecyclerViewItems.add(ru);

                    }
                    adapter.notifyDataSetChanged();
                    empty.setVisibility(View.GONE);
                }
                else
                    empty.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
                rfs.setEnabled(true);
                rfs.setVisibility(View.VISIBLE);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                empty.setVisibility(View.VISIBLE);
                rfs.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            }
        };

        mDatabase.addListenerForSingleValueEvent(mListener);

        cListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int tc = (int) dataSnapshot.getChildrenCount();
                    rc.setText(String.format(Locale.US,"My Referral (%d)",tc));
                    if(mRecyclerViewItems.size()<tc){
                        int extra= tc-mRecyclerViewItems.size();
                        more.setText(String.format(Locale.US,"+ %d more...",extra));
                        more.setVisibility(View.VISIBLE);
                    }
                    else
                        more.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        cDatabase.addListenerForSingleValueEvent(cListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(InviteActivity.this, AuthActivity.class));
            finish();
        }

        iListener =new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    iTitle.setText(Objects.toString(dataSnapshot.child("title").getValue(),null));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        iBody.setText(Html.fromHtml(Objects.toString(dataSnapshot.child("body").getValue(),null),Html.FROM_HTML_MODE_LEGACY));
                    }
                    else
                        iBody.setText(Html.fromHtml(Objects.toString(dataSnapshot.child("body").getValue(),null)));


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        iDatabase.addValueEventListener(iListener);


    }

    @Override
    public void onBackPressed() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {

        if (mListener != null) {
            mDatabase.removeEventListener(mListener);
        }
        if (iListener != null) {
            iDatabase.removeEventListener(iListener);
        }
        if (cListener != null) {
            cDatabase.removeEventListener(cListener);
        }
        super.onDestroy();
    }


}
