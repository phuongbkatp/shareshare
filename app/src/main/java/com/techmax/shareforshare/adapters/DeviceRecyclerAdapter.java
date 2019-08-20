package com.techmax.shareforshare.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.model.UserProfiles;

import java.util.List;

/**
 * Created by ABC on 9/2/2017.
 */

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // An Activity's Context.
    private final Context mContext;

    // The list of Native Express ads and menu items.
    private final List<UserProfiles> mRecyclerViewItems;


    public DeviceRecyclerAdapter(Context context, List<UserProfiles> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView dn,ds;
        AppCompatImageView iv;

        DeviceViewHolder(View view) {

            super(view);
            mView=view;
            dn=(TextView)mView.findViewById(R.id.device_name);
            ds=(TextView)mView.findViewById(R.id.device_status);
            iv=(AppCompatImageView)mView.findViewById(R.id.device_light);

        }


        public void setDeviceName(String deviceName) {
            dn.setText(deviceName);
        }

        public void setDeviceStatus(boolean deviceStatus) {
            if(deviceStatus){
                ds.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                ds.setText("Online");
                iv.setImageResource(R.drawable.ic_smartphone_active);
            }
            else{
                ds.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                ds.setText("Offline");
                iv.setImageResource(R.drawable.ic_smartphone_off);

            }
        }
    }


    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.device_item, viewGroup, false);
        return new DeviceViewHolder(menuItemLayoutView);

    }

    /**
     *  Replaces the content in the views that make up the menu item view and the
     *  Native Express ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        DeviceViewHolder deviceItemHolder = (DeviceViewHolder) holder;
        UserProfiles up = mRecyclerViewItems.get(position);
        deviceItemHolder.setDeviceName(up.getUdn());
        deviceItemHolder.setDeviceStatus(up.isUds());


    }
}