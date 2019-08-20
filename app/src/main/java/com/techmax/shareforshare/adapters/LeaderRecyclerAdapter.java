package com.techmax.shareforshare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.model.EarningProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ABC on 8/26/2017.
 */

public class LeaderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MENU_ITEM_VIEW_TYPE = 0;



    // An Activity's Context.
    private final Context mContext;

    // The list of Native Express ads and menu items.
    private final List<Object> mRecyclerViewItems;


    public LeaderRecyclerAdapter(Context context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;

    }

    public class LeaderViewHolder extends RecyclerView.ViewHolder {
        View mView;
        CircleImageView ci;
        TextView tv,rt,te,ts;

        LeaderViewHolder(View view) {

            super(view);
            mView=view;
            ci=(CircleImageView)mView.findViewById(R.id.circleImageLeader);
            tv=(TextView)mView.findViewById(R.id.userName);
            rt=(TextView)mView.findViewById(R.id.userRank);
            te=(TextView)mView.findViewById(R.id.userEarn);

        }

        public void setImage(String image) {
            Picasso.with(mContext).load(image).placeholder(R.drawable.user).fit().centerCrop().into(ci);
        }

        public void setName(String name) {

            tv.setText(name);
        }

        public void setRank(int rank) {
            rt.setText(String.format(Locale.US,"%d",rank));
        }

        public void setEarning(double earning) {
            te.setText(String.format(Locale.US,"%.8f BTC",earning));
        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }



    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {

         return MENU_ITEM_VIEW_TYPE;

    }

    /**
     * Creates a new view for a menu item view or a Native Express ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.leaderboard_item, viewGroup, false);
                return new LeaderViewHolder(menuItemLayoutView);

    }

    /**
     *  Replaces the content in the views that make up the menu item view and the
     *  Native Express ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                LeaderViewHolder leaderItemHolder = (LeaderViewHolder) holder;
                EarningProfile ep = (EarningProfile) mRecyclerViewItems.get(position);

                leaderItemHolder.setImage(ep.getEui());
                leaderItemHolder.setName(ep.getEun());
                leaderItemHolder.setRank(mRecyclerViewItems.size()-position);
                leaderItemHolder.setEarning(ep.getEt());


                if (ep.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    leaderItemHolder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                } else
                    leaderItemHolder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorWindowUpper));


    }

}


