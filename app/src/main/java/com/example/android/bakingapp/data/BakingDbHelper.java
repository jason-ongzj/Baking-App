package com.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ben on 9/14/2017.
 */

public class BakingDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bakingRecipes.db";

    private static final int DATABASE_VERSION = 1;

    public BakingDbHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_RECIPES_TABLE =

                "CREATE TABLE " + BakingContract.BakingEntry.RECIPE_TABLE + " (" +
                        BakingContract.BakingEntry._ID + " INT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_SERVINGS + " INT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_IMAGE + " TEXT NOT NULL);";

        final String SQL_CREATE_INGREDIENTS_TABLE =

                "CREATE TABLE " + BakingContract.BakingEntry.INGREDIENTS_TABLE + " (" +
                        BakingContract.BakingEntry.RECIPE_INGREDIENT + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_INGREDIENTS + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_QUANTITY + " REAL NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_MEASURE + " TEXT NOT NULL);";

        final String SQL_CREATE_STEPS_TABLE =

                "CREATE TABLE " + BakingContract.BakingEntry.STEPS_TABLE + " (" +
                        BakingContract.BakingEntry.RECIPE_STEP  + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, " +
                        BakingContract.BakingEntry.COLUMN_STEP_ID + " INT NOT NULL);";

        db.execSQL(SQL_CREATE_RECIPES_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
        db.execSQL(SQL_CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
