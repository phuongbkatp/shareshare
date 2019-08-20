package com.techmax.shareforshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.techmax.shareforshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

public class TsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView info,cache,vr;

    private LinearLayout help,faq,privacy;
    private Switch sw;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ValueEventListener mListener;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "btc_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ts);

        mAuth= FirebaseAuth.getInstance();

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        toolbar=(Toolbar)findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Support");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        info=findViewById(R.id.info);
        help=findViewById(R.id.mailSupport);
        faq=findViewById(R.id.faq);
        cache=findViewById(R.id.cache);
        vr=findViewById(R.id.version);
        sw=findViewById(R.id.sw);
        privacy=findViewById(R.id.privacy);
        initializeCache();

        setValues();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("support");

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TsActivity.this,FaqActivity.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                String subject=getString(R.string.app_name)+" Support : " +mAuth.getCurrentUser().getDisplayName();
                String body="User : "+mAuth.getCurrentUser().getEmail()+" | "+mAuth.getCurrentUser().getUid()+"\n\n ";
                Uri data = Uri.parse("mailto:"+getString(R.string.support_email)+"?subject=" + subject + "&body=" + body);
                intent.setData(data);
                startActivity(intent);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(TsActivity.this,PrivacyActivity.class));

            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true){
                    setBoolean("notifications",true);
                }else{
                    setBoolean("notifications",false);
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        mListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        info.setText(Html.fromHtml(Objects.toString(dataSnapshot.child("info").getValue(),null),Html.FROM_HTML_MODE_LEGACY));
                    }
                    else
                        info.setText(Html.fromHtml(Objects.toString(dataSnapshot.child("info").getValue(),null)));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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


    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        cache.setText(readableFileSize(size));

    }

    public long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void setValues() {
        if (!getBoolean("notifications")){
            sw.setChecked(false);
        }else{
            sw.setChecked(true);
        }
        try {
            PackageInfo pInfo =getPackageManager().getPackageInfo(getPackageName(),0);
            String version = pInfo.versionName;
            vr.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void setBoolean(String PREF_NAME,Boolean val) {
        editor.putBoolean(PREF_NAME, val);
        editor.commit();
    }
    public boolean getBoolean(String PREF_NAME) {
        return pref.getBoolean(PREF_NAME,true);
    }
}
