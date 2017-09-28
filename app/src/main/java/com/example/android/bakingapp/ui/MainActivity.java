package com.example.android.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ListView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.BakingDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    @Nullable
    @BindView(R.id.recipe_list_view) ListView mListView;
    @Nullable
    @BindView(R.id.recipe_grid_view) GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BakingDbHelper dbHelper = new BakingDbHelper(this);
        dbHelper.getWritableDatabase();
    }
}
