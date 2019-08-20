package com.techmax.shareforshare.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techmax.shareforshare.R;
import com.techmax.shareforshare.model.Notemodel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ABC on 9/3/2017.
 */

public class NoteRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int HEADER_ITEM_VIEW_TYPE = 1;

    // An Activity's Context.
    private final Context mContext;

    // The list of Native Express ads and menu items.
    private final List<Object> mRecyclerViewItems;

    private int mExpandedPosition=-1;




    public NoteRecyclerAdapter(Context context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;

    }

    public class HeaderHolder extends RecyclerView.ViewHolder{

        TextView noteDate;
        View mView;

        public HeaderHolder(View itemView) {
            super(itemView);
            mView=itemView;
            noteDate=mView.findViewById(R.id.noteDate);
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        View mView;
        AppCompatImageView ci;
        TextView nh,nb,nt;

        NoteViewHolder(View view) {

            super(view);
            mView=view;
            ci=(AppCompatImageView) mView.findViewById(R.id.noteFlag);
            nh=(TextView)mView.findViewById(R.id.noteTitle);
            nb=(TextView)mView.findViewById(R.id.noteBody);
            nt=(TextView)mView.findViewById(R.id.noteTime);

        }

        public void setIcon(String icon) {
            switch (icon){
                case "PAYOUT":
                    ci.setImageResource(R.drawable.ic_wallet);
                    break;
                case "REFER":
                    ci.setImageResource(R.drawable.ic_referral);
                    break;
                default:
                    ci.setImageResource(R.drawable.ic_notifications);
            }

        }

        public void setTitle(String title) {
            nh.setText(title);
        }


        public void setBody(String body) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                nb.setText(Html.fromHtml(body,Html.FROM_HTML_MODE_LEGACY));
            }
            else
                nb.setText(Html.fromHtml(body));
        }

        public void setTime(long time) {
           // nt.setText(DateUtils.getRelativeTimeSpanString(time,new Date().getTime(),DateUtils.SECOND_IN_MILLIS));
            nt.setText(getTime(time));
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

        if(mRecyclerViewItems.get(position).getClass()==Long.class)
           return HEADER_ITEM_VIEW_TYPE;
        else
            return MENU_ITEM_VIEW_TYPE;


    }

    /**
     * Creates a new view for a menu item view or a Native Express ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if(viewType==MENU_ITEM_VIEW_TYPE) {
            View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.notification_item, viewGroup, false);
            return new NoteViewHolder(menuItemLayoutView);
        }
        else {
            View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.header_sub, viewGroup, false);
            return new HeaderHolder(menuItemLayoutView);
        }


    }

    /**
     *  Replaces the content in the views that make up the menu item view and the
     *  Native Express ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(getItemViewType(position)==HEADER_ITEM_VIEW_TYPE)
        {
            HeaderHolder headerHolder= (HeaderHolder) holder;
            headerHolder.noteDate.setText(getDate((Long) mRecyclerViewItems.get(position)));
        }
        if (getItemViewType(position)==MENU_ITEM_VIEW_TYPE) {
            NoteViewHolder noteItemHolder = (NoteViewHolder) holder;
            Notemodel ep = (Notemodel) mRecyclerViewItems.get(position);

            final boolean isExpanded = position == mExpandedPosition;
            noteItemHolder.nb.setEllipsize(isExpanded ? null : TextUtils.TruncateAt.END);
            noteItemHolder.nb.setMaxLines(isExpanded ? Integer.MAX_VALUE : 1);
            noteItemHolder.itemView.setActivated(isExpanded);
            noteItemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandedPosition = isExpanded ? -1 : position;
                    notifyDataSetChanged();
                }
            });

            noteItemHolder.setIcon(ep.getType());
            noteItemHolder.setTitle(ep.getTitle());
            noteItemHolder.setBody(ep.getBody());
            noteItemHolder.setTime(ep.getTime());


        }
    }
    private String getDate(long milliSeconds) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(milliSeconds);
        String date = DateFormat.format("dd MMMM ''yy", cal).toString();
        return date;
    }

    private String getTime(long milliSeconds) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(milliSeconds);
        String date = DateFormat.format("hh:mm a", cal).toString();
        return date;
    }

}



