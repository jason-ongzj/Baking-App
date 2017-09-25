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

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends BaseAdapter {

    @BindView(R.id.card_display_text) TextView cardDisplayText;
    @BindView(R.id.card_display) CardView cardView;

    private Context mContext;
    private ArrayList<String> mRecipes;
    private LayoutInflater mInflater;
    public void setRecipes(ArrayList<String> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
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
        if (convertView == null) {
            View child = mInflater.inflate(R.layout.item_display, null);
            ButterKnife.bind(this, child);
            cardDisplayText.setText(mRecipes.get(position));
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
