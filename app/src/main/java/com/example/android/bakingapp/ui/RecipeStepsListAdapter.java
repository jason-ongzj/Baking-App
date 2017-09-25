package com.example.android.bakingapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDisplayActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecipeStepsListAdapter extends
        RecyclerView.Adapter<RecipeStepsListAdapter.RecipeStepAdapterViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private String mRecipe;

    final private RecipeInstructionOnClickHandler mClickHandler;

    public interface RecipeInstructionOnClickHandler{
        void onInstructionsClicked(String description, String uriString,
                                   String thumbnailUri, int position);
    }

    public RecipeStepsListAdapter(RecipeInstructionOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public void setContext(Context context){
        mContext = context;
    }

    public void setRecipe(String recipe){
        mRecipe = recipe;
    }

    public String getRecipe(){
        return mRecipe;
    }

    public class RecipeStepAdapterViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        private String mUriString;
        private String mDescription;
        private String mThumbnailUri;

        @BindView(R.id.card_display_text) TextView mRecipeStep;

        public RecipeStepAdapterViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.card_display_text)
        public void onClick(View v) {
            returnDataFromDb();
            mClickHandler.onInstructionsClicked(mDescription, mUriString,
                    mThumbnailUri, getAdapterPosition());
        }

        private void returnDataFromDb(){
            mCursor.moveToPosition(getAdapterPosition());
            mDescription = mCursor.getString(RecipeDisplayActivity.INDEX_DESCRIPTION);
            mUriString = mCursor.getString(RecipeDisplayActivity.INDEX_VIDEO_URL);
            mThumbnailUri = mCursor.getString(RecipeDisplayActivity.INDEX_THUMBNAIL_URL);
        }
    }

    @Override
    public RecipeStepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_display, parent, false);
        return new RecipeStepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mRecipeStep.setText(mCursor.getString(RecipeDisplayActivity.INDEX_SHORT_DESCRIPTION));
    }

    @Override
    public int getItemCount() {
        if(null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
