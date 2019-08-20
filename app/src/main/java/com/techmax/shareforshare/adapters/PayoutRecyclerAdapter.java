package com.techmax.shareforshare.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.TransactionActivity;
import com.techmax.shareforshare.model.Payout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ABC on 9/12/2017.
 */

public class PayoutRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MENU_ITEM_VIEW_TYPE = 0;


    // An Activity's Context.
    private final Context mContext;

    // The list of Native Express ads and menu items.
    private final List<Object> mRecyclerViewItems;


    public PayoutRecyclerAdapter(Context context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    public class PayoutViewHolder extends RecyclerView.ViewHolder {
        View mView;
        AppCompatImageView ci;
        TextView nh,nb,nt;

        PayoutViewHolder(View view) {

            super(view);
            mView=view;
            ci=(AppCompatImageView) mView.findViewById(R.id.noteFlag);
            nh=(TextView)mView.findViewById(R.id.payDate);
            nb=(TextView)mView.findViewById(R.id.noteBody);
            nt=(TextView)mView.findViewById(R.id.noteTitle);

        }


        public void setTime(long time)  {

            nh.setText(getDate(time));
        }


        public void setBody(String body) {
            nb.setText(body);
        }

        public void setStatus(int status) {

            switch(status){
                case 0:
                    ci.setImageResource(R.drawable.ic_pending);
                    nt.setText(R.string.pending);
                    break;
                case 1:
                    ci.setImageResource(R.drawable.ic_check);
                    nt.setText(R.string.success);
                    break;
                case -1:
                    ci.setImageResource(R.drawable.ic_warning);
                    nt.setText(R.string.failed);
                    break;
                default:
                    ci.setImageResource(R.drawable.ic_pending);
                    nt.setText(R.string.pending);

            }

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
        return  MENU_ITEM_VIEW_TYPE;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.payout_item, viewGroup, false);
                return new PayoutRecyclerAdapter.PayoutViewHolder(menuItemLayoutView);


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                PayoutRecyclerAdapter.PayoutViewHolder payoutItemHolder = (PayoutRecyclerAdapter.PayoutViewHolder) holder;
                Payout ep = (Payout) mRecyclerViewItems.get(position);

                payoutItemHolder.setTime(ep.getPt());
                payoutItemHolder.setBody("Amount: "+String.format(Locale.US,"%.5f BTC",ep.getPa()));
                payoutItemHolder.setStatus(ep.getPs());
                payoutItemHolder.mView.setTag(ep.getKey());

                payoutItemHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i=new Intent(mContext, TransactionActivity.class);
                        i.putExtra("txn_id", (String) v.getTag());
                        mContext.startActivity(i);
                    }
                });


    }

    private String getDate(long milliSeconds) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(milliSeconds);
        return DateFormat.format("dd MMMM ''yy", cal).toString();
    }

}
