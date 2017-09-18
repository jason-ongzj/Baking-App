package com.example.android.bakingapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;

/**
 * Created by Ben on 9/16/2017.
 */

public class RecipeStepsListAdapter extends
        RecyclerView.Adapter<RecipeStepsListAdapter.RecipeStepAdapterViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public RecipeStepsListAdapter(Context context){
        mContext = context;
    }

    public class RecipeStepAdapterViewHolder extends RecyclerView.ViewHolder
            {

        public final CardView mCardDisplay;
        public final TextView mRecipeName;

        public RecipeStepAdapterViewHolder(View view){
            super(view);
            mCardDisplay = (CardView) view.findViewById(R.id.card_display);
            mRecipeName = (TextView) view.findViewById(R.id.card_display_text);
        }
    }

    @Override
    public RecipeStepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForCardDisplayItem = R.layout.item_display;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForCardDisplayItem, parent, false);
        return new RecipeStepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapterViewHolder holder, int position) {
        holder.mRecipeName.setText("Hello World " + Integer.toString(position));
    }

    @Override
    public int getItemCount() {
        if(null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
    }

    public Cursor getCursor(){
        return mCursor;
    }
}
