package com.techmax.shareforshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.techmax.shareforshare.R;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends BaseActivity implements InstallReferrerStateListener {
    private SignInButton googleBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private TelephonyManager tm;

    private TextView aggrement;
    private String referrer;
    private InstallReferrerClient mReferrerClient;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "btc_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        googleBtn = (SignInButton) findViewById(R.id.googleBtn);
        googleBtn.setSize(SignInButton.SIZE_WIDE);
        googleBtn.setColorScheme(SignInButton.COLOR_DARK);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_auth))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(AuthActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        SpannableString ss = new SpannableString(getString(R.string.agree));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(AuthActivity.this,PrivacyActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };


        ss.setSpan(clickableSpan, 91, 105, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        aggrement = (TextView) findViewById(R.id.aggrement);
        aggrement.setText(ss);
        aggrement.setMovementMethod(LinkMovementMethod.getInstance());
        aggrement.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                showProgressDialog();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    onAuthSuccess(user);
                }
                else {
                    hideProgressDialog();
                    Toast.makeText(AuthActivity.this, "Authentication Failed.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void onAuthSuccess(FirebaseUser user) {

        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    addUser(Long.parseLong(dataSnapshot.child("uc").getValue().toString()), Integer.parseInt(dataSnapshot.child("us").getValue().toString()), dataSnapshot.child("ur").getValue().toString(), true);
                } else if (referrer != null && referrer.length() >= 28 && referrer.matches("[A-Za-z0-9]*")) {
                    addUser(0, 1, referrer, false);
                } else
                    addUser(0, 1, "", false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addUser(long uc, int us, String ur, boolean exist) {

        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        String ue = user.getEmail();
        String un = user.getDisplayName();
        String ui = user.getPhotoUrl().toString();

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("un", un);
        userMap.put("ue", ue);
        userMap.put("ui", ui);
        if (uc == 0)
            userMap.put("uc", ServerValue.TIMESTAMP);
        else
            userMap.put("uc", uc);
        userMap.put("ul", ServerValue.TIMESTAMP);
        userMap.put("us", us);
        userMap.put("ur", ur);

        final String installer = getPackageManager().getInstallerPackageName(getPackageName());

        String uiid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("utok", FirebaseInstanceId.getInstance().getToken());
        profileMap.put("udn", Build.MODEL);
        profileMap.put("udl", ServerValue.TIMESTAMP);
        profileMap.put("uds", true);
        profileMap.put("udc", tm.getSimCountryIso().toUpperCase());
        profileMap.put("udi", installer != null ? installer : "");

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            profileMap.put("av", pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Map requestMap = new HashMap();
        requestMap.put("users/" + uid, userMap);
        requestMap.put("userprofiles/" + uid + "/" + uiid, profileMap);
        requestMap.put("dc/" + uiid + "/devices/" + uid, true);



        mDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
                if (databaseError != null) {
                    Toast.makeText(AuthActivity.this, "Server Error Occured", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                if(!getBoolean("MAP_DONE")) {
                    try {
                        ReferrerDetails response = mReferrerClient.getInstallReferrer();
                        referrer = response.getInstallReferrer();
                        setBoolean("MAP_DONE", true);
                        mReferrerClient.endConnection();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                break;
            default:
                ;
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {

    }

    public void setBoolean(String PREF_NAME,Boolean val) {
        editor.putBoolean(PREF_NAME, val);
        editor.commit();
    }
    public boolean getBoolean(String PREF_NAME) {
        return pref.getBoolean(PREF_NAME,false);
    }
}
