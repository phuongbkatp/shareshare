package com.techmax.shareforshare;

import android.provider.Settings;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ABC on 8/8/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth= FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("userprofiles");
    }

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        if(mAuth.getCurrentUser()!=null) {
            String uiid= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            mDatabase.child(mAuth.getCurrentUser().getUid()).child(uiid).child("utok").setValue(refreshedToken);
        }
    }
}
