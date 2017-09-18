package com.example.android.bakingapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import java.util.ArrayList;

import static com.example.android.bakingapp.MainActivity.TAG;

/**
 * Created by Ben on 9/17/2017.
 */

public class RecipeListAdapter extends BaseAdapter {

    private Context mContext;
    private Cursor mCursor;
    private ArrayList<String> mRecipes;
    private LayoutInflater mInflater;
    public void setRecipes(ArrayList<String> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
        Log.d(TAG, "setRecipes: " + mRecipes.get(3));
    }

    public RecipeListAdapter(Context context){
        mContext = context;
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public void setInflater(LayoutInflater inflater){
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        if (null == mRecipes) return 0;
        return mRecipes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        CardView cardView;
//        mCursor.moveToPosition(position);
        if (convertView == null) {
            View child = mInflater.inflate(R.layout.item_display, null);
            cardView = (CardView) child.findViewById(R.id.card_display);
            textView = (TextView) child.findViewById(R.id.card_display_text);
//            if (mRecipes != null)
//            textView.setText(mCursor.getString(MainActivity.INDEX_RECIPE_NAME));
            textView.setText(mRecipes.get(position));
        } else {
            cardView = (CardView) convertView;
        }
        return cardView;
    }
}
