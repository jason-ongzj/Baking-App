package com.example.android.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BakingProvider extends ContentProvider {

    private static final int RECIPES = 100;
    private static final int RECIPES_WITH_ID = 101;

    private static final int INGREDIENTS = 200;
    private static final int INGREDIENTS_WITH_RECIPE_NAME = 201;

    private static final int STEPS = 300;
    private static final int STEPS_WITH_RECIPE_NAME = 301;

    private BakingDbHelper mBakingDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BakingContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BakingContract.PATH_RECIPES, RECIPES);
        matcher.addURI(authority, BakingContract.PATH_RECIPES + "/#", RECIPES_WITH_ID);

        matcher.addURI(authority, BakingContract.PATH_INGREDIENTS, INGREDIENTS);
        matcher.addURI(authority, BakingContract.PATH_INGREDIENTS + "/*", INGREDIENTS_WITH_RECIPE_NAME);

        matcher.addURI(authority, BakingContract.PATH_STEPS, STEPS);
        matcher.addURI(authority, BakingContract.PATH_STEPS + "/*", STEPS_WITH_RECIPE_NAME);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mBakingDbHelper = new BakingDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mBakingDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch(match){
            case RECIPES:
                retCursor = db.query(BakingContract.BakingEntry.RECIPE_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECIPES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                retCursor = db.query(BakingContract.BakingEntry.RECIPE_TABLE,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mBakingDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case RECIPES:
                long id = db.insert(BakingContract.BakingEntry.RECIPE_TABLE, null, contentValues);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(BakingContract.BakingEntry.RECIPE_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case INGREDIENTS:
                id = db.insert(BakingContract.BakingEntry.INGREDIENTS_TABLE, null, contentValues);
                if (id > 0){
                    returnUri = Uri.withAppendedPath(BakingContract.BakingEntry.INGREDIENTS_URI,
                                    contentValues.get(BakingContract.BakingEntry.RECIPE_INGREDIENT).toString());
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case STEPS:
                id = db.insert(BakingContract.BakingEntry.STEPS_TABLE, null, contentValues);
                if (id > 0){
                    returnUri = Uri.withAppendedPath(BakingContract.BakingEntry.STEPS_URI,
                            contentValues.get(BakingContract.BakingEntry.RECIPE_STEP).toString());
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mBakingDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted = 0;

        switch(match){
            case RECIPES:
                tasksDeleted = db.delete(BakingContract.BakingEntry.RECIPE_TABLE, null, null);
                break;
            case INGREDIENTS:
                tasksDeleted = db.delete(BakingContract.BakingEntry.INGREDIENTS_TABLE, null, null);
                break;
            case STEPS:
                tasksDeleted = db.delete(BakingContract.BakingEntry.STEPS_TABLE, null, null);
                break;
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
