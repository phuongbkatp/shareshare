package com.techmax.shareforshare;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.techmax.shareforshare.R;


public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Hold on! Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void setProgressMessage(String msg){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(msg);
        }

    }

}
