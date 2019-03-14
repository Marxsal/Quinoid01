package com.example.my32ndapplication;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.my32ndapplication.TwResourceFragment.OnListFragmentInteractionListener;
//import com.example.my32ndapplication.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TwResourceRecyclerViewAdapter extends RecyclerView.Adapter<TwResourceRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<TwResource> mTwResources;
    private final OnListFragmentInteractionListener mListener;
    public static final String LOG_TAG = "32X TwResRecyclVuAdaptr";
    public TwResourceRecyclerViewAdapter(ArrayList<TwResource> items, OnListFragmentInteractionListener listener) {
        mTwResources = items;
        mListener = listener;
        Log.d(LOG_TAG, "adapter - I see # of items:" + items.size());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_twresource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(LOG_TAG, "onBindViewHolder. See pos # " + position);
        holder.mTwResource = mTwResources.get(position);
        holder.mTitleView.setText(mTwResources.get(position).getTitle());
        holder.mDescriptionView.setText(mTwResources.get(position).getDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mTwResource);
                }
            }
        });
    }




    @Override
    public int getItemCount() {
        return mTwResources.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public TwResource mTwResource;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            // The url is the id, and we don't display that
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mTitleView = (TextView) view.findViewById(R.id.tw_resource_title);
            mDescriptionView = (TextView) view.findViewById(R.id.tw_resource_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
