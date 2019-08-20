package com.techmax.shareforshare;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ABC on 8/8/2017.
 */

public class ShareApplication extends Application {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static ShareApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        mAuth= FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {



                }
                else{
                    String uiid= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("userprofiles").child(mAuth.getCurrentUser().getUid()).child(uiid);


                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                mDatabase.child("uds").onDisconnect().setValue(false);
                                mDatabase.child("uds").setValue(true);
                                PackageInfo pInfo;
                                try {
                                    pInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                                    mDatabase.child("av").setValue(pInfo.versionCode);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);



    }





    public static synchronized ShareApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}


