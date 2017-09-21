package com.example.android.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDisplayActivity;

import java.util.ArrayList;

public class RecipeListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mRecipes;
    private LayoutInflater mInflater;
    public void setRecipes(ArrayList<String> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
//        Log.d(TAG, "setRecipes: " + mRecipes.get(3));
    }

    public RecipeListAdapter(Context context){
        mContext = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView textView;
        CardView cardView;
        if (convertView == null) {
            View child = mInflater.inflate(R.layout.item_display, null);
            cardView = (CardView) child.findViewById(R.id.card_display);
            textView = (TextView) child.findViewById(R.id.card_display_text);
            textView.setText(mRecipes.get(position));
        } else {
            cardView = (CardView) convertView;
        }
        cardView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(mContext, RecipeDisplayActivity.class);
                intent.putExtra("recipe", mRecipes.get(position));
                mContext.startActivity(intent);
            }
        });
        return cardView;
    }
}
