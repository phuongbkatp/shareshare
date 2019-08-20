package com.techmax.shareforshare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.model.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ABC on 8/29/2017.
 */

public class ReferralRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // An Activity's Context.
    private final Context mContext;

    // The list of Native Express ads and menu items.
    private final List<Users> mRecyclerViewItems;


    public ReferralRecyclerAdapter(Context context, List<Users> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    public class ReferralViewHolder extends RecyclerView.ViewHolder {
        View mView;
        CircleImageView ci;
        TextView tn;

        ReferralViewHolder(View view) {

            super(view);
            mView=view;
            ci=(CircleImageView)mView.findViewById(R.id.rc_image);
            tn=(TextView)mView.findViewById(R.id.rc_name);

        }

        public void setImage(String image) {
            Picasso.with(mContext).load(image).fit().centerCrop().into(ci);
        }

        public void setName(String name) {

            tn.setText(name);
        }


    }


    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.referral_item, viewGroup, false);
                return new ReferralViewHolder(menuItemLayoutView);

    }

    /**
     *  Replaces the content in the views that make up the menu item view and the
     *  Native Express ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ReferralViewHolder referralItemHolder = (ReferralViewHolder) holder;
                Users ru = mRecyclerViewItems.get(position);
                referralItemHolder.setImage(ru.getUi());
                referralItemHolder.setName(ru.getUn());


    }
}
