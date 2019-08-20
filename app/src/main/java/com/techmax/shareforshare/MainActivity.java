package com.techmax.shareforshare;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.techmax.shareforshare.R;
import com.github.guilhe.circularprogressview.CircularProgressView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener, RewardedVideoAdListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, eDatabase, uDatabase;
    private ValueEventListener earningListener, appListerner, userListener;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;

    private CircleImageView customPic;
    private TextView ue, mainName, ecr, claimed, referral, activeu, appv, userStatus, gn, un, ecount;
    private Button watch;


    private CircularProgressView cp;

    private PrefUtils prefUtils;
    private int timeToStart;
    private TimerState timerState;
    private CountDownTimer timer1;

    private Button btn, updateNow;
    private CardView updateView;

    private float r;
    private TelephonyManager tm;
    private int currentCode, us = 0, MAX_TIME = 350, tcrl = 350, MAX_ENERGY = 50, en = 0;
    private Snackbar snackbar;
    private boolean ic = true;

    private CardView earning;


    private IntentFilter filter;

    private ConnectivityReceiver receiver;

    private NumberFormat nf;
    private DecimalFormat df;


    private Boolean DialogOpened = false,isVisible=false;
    private TextView text_view_go_pro;
    private Dialog dialog;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;

    private ProgressBar energy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);



        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        receiver = new ConnectivityReceiver();
        filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

        prefUtils = new PrefUtils(getApplicationContext());
        snackbar = Snackbar.make(findViewById(R.id.root), R.string.internet_msg, Snackbar.LENGTH_INDEFINITE);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        un = navHeader.findViewById(R.id.un);
        ue = navHeader.findViewById(R.id.ue);


        setUpNavigationView();

        MobileAds.initialize(this, getString(R.string.admob_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        if(!mRewardedVideoAd.isLoaded())
        loadRewardedVideoAd();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_main));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });


        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        df = new DecimalFormat("#,##,##,###");



        if (checkConnection()) showProgressDialog();
        else hideProgressDialog();



    }



    @Override
    protected void onResume() {
        isVisible=true;
        if(!mInterstitialAd.isLoaded())
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mRewardedVideoAd.resume(this);

        ShareApplication.getInstance().setConnectivityListener(this);
        registerReceiver(receiver, filter);
        super.onResume();
    }


    @Override
    protected void onPause() {
        isVisible=false;
        unregisterReceiver(receiver);
        mRewardedVideoAd.pause(this);

        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else if (timerState == TimerState.RUNNING)
            moveTaskToBack(true);
        else super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();

        } else {

            mDatabase = FirebaseDatabase.getInstance().getReference().child("appprofile");
            eDatabase = FirebaseDatabase.getInstance().getReference().child("earningprofile").child("");
            uDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("");

            earningListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        animateTextView((float) Double.parseDouble(ecr.getText().toString()), (float) Double.parseDouble(Objects.toString(dataSnapshot.child("ecr").getValue(), "0")), ecr);
                        animateTextView((float) Double.parseDouble(claimed.getText().toString()), (float) Double.parseDouble(Objects.toString(dataSnapshot.child("ec").getValue(), "0")), claimed);
                        animateTextView((float) Double.parseDouble(referral.getText().toString()), (float) Double.parseDouble(Objects.toString(dataSnapshot.child("er").getValue(), "0")), referral);
                        en = (int) Long.parseLong(Objects.toString(dataSnapshot.child("eeb").getValue(), "0"));
                        energy.setProgress(en);

                        ecount.setText(en+"/"+MAX_ENERGY);

                        if (en < 1 && mRewardedVideoAd.isLoaded())
                            watch.setVisibility(View.VISIBLE);
                        else
                            watch.setVisibility(View.GONE);

                    }

                    hideProgressDialog();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();

                }
            };

            eDatabase.addValueEventListener(earningListener);

            appListerner = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String uc = Objects.toString(dataSnapshot.child("usercount").getValue(), null);
                        if (uc != null)
                            activeu.setText(df.format(Integer.parseInt(uc)));

                        try {
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                            currentCode = pInfo.versionCode;


                        } catch (PackageManager.NameNotFoundException e) {

                        }
                        String fupdate = Objects.toString(dataSnapshot.child("fupdate").getValue(), null);
                        if (dataSnapshot.child("ic").exists())
                        ic = (boolean) dataSnapshot.child("ic").getValue();

                        if (fupdate != null && Integer.parseInt(fupdate) > currentCode && !DialogOpened) {

                            showUpdateDialog();
                        }
                        String rupdate = Objects.toString(dataSnapshot.child("rupdate").getValue(), null);
                        if (rupdate != null && Integer.parseInt(rupdate) > currentCode)
                            updateView.setVisibility(View.VISIBLE);
                        else
                            updateView.setVisibility(View.GONE);

                        if (dataSnapshot.child("timer").exists()) {
                            tcrl = Integer.parseInt(Objects.toString(dataSnapshot.child("timer").getValue(), null));
                        }


                        String gnm = Objects.toString(dataSnapshot.child("gn").getValue(), null);

                        if (gnm != null && !gnm.equals("NA")) {
                            gn.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                gn.setText(Html.fromHtml(gnm, Html.FROM_HTML_MODE_LEGACY));
                            } else
                                gn.setText(Html.fromHtml(gnm));
                        } else
                            gn.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mDatabase.addValueEventListener(appListerner);

            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        us = Integer.parseInt(Objects.toString(dataSnapshot.child("us").getValue(), null));
                        if (us == 0) {
                            userStatus.setText(R.string.suspended);
                            userStatus.setBackgroundResource(R.drawable.suspend_bg);
                            btn.setEnabled(false);
                            if (timer1 != null) {
                                timer1.cancel();
                            }
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mAuth.signOut();

                                    startActivity(new Intent(MainActivity.this, AuthActivity.class));
                                    finish();
                                }
                            }, 10000);
                        } else {
                            userStatus.setBackgroundResource(R.drawable.active_bg);
                            userStatus.setText(R.string.active);
                            if (en < 1)
                                btn.setEnabled(false);
                            else
                                btn.setEnabled(true);
                        }


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            uDatabase.addValueEventListener(userListener);


        }

    }

    private void showUpdateDialog() {

        this.dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_update);
        this.text_view_go_pro = (TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String apn = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apn)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apn)));
                }
            }
        });
        dialog.show();
        DialogOpened = true;

    }


    @Override
    protected void onDestroy() {
        if (timer1 != null) {
            timer1.cancel();
        }
        if (earningListener != null) {
            eDatabase.removeEventListener(earningListener);
        }
        if (earningListener != null) {
            eDatabase.removeEventListener(earningListener);
        }
        if (appListerner != null) {
            mDatabase.removeEventListener(appListerner);
        }
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.my_profile_menu) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (i == R.id.notification_menu) {
            startActivity(new Intent(this, NotificationActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_leader:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, LeaderBoardActivity.class));
                        break;

                    case R.id.nav_balance:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, BalanceActivity.class));
                        break;
                    case R.id.nav_payout_history:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, PayoutActivity.class));
                        break;
                    case R.id.nav_notifications:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                        break;
                    case R.id.nav_invite:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, InviteActivity.class));
                        break;

                    case R.id.nav_support:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, TsActivity.class));
                        break;

                    case R.id.sign_out_menu:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, AuthActivity.class));
                        finish();
                        break;

                    default:
                        break;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);


                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        drawer.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    public void animateTextView(float initialValue, float finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(initialValue, finalValue);
        valueAnimator.setDuration(1500);
        valueAnimator.setInterpolator(new DecelerateInterpolator(0.8f));

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(String.format(Locale.US, "%.8f", valueAnimator.getAnimatedValue()));

            }
        });
        valueAnimator.start();

    }


    @Override
    protected void onStop() {
        super.onStop();
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
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        } else {

            if (snackbar.isShownOrQueued())
                snackbar.dismiss();

        }

    }


    public void GoToProfile(View view) {
        Intent sharedIntent = new Intent(MainActivity.this, ProfileActivity.class);
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(customPic, "pImage");
        pairs[1] = new Pair<View, String>(mainName, "pName");
        pairs[2] = new Pair<View, String>(userStatus, "pStatus");
        ActivityOptions op = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
        startActivity(sharedIntent, op.toBundle());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (en == 0) {
            watch.setVisibility(View.VISIBLE);
        } else
            watch.setVisibility(View.GONE);
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();

    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.admob_rewarded),
                new AdRequest.Builder().build());
    }

    private enum TimerState {
        STOPPED,
        RUNNING
    }


}
