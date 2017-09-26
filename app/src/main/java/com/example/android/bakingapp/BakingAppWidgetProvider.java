package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.bakingapp.ui.RecipeDisplayActivity;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidgetProvider extends AppWidgetProvider {

    private static RemoteViews mRemoteViews;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String ingredientsList, String recipeName) {

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget_provider);
        mRemoteViews.setTextViewText(R.id.recipeName, recipeName);
        mRemoteViews.setTextViewText(R.id.ingredientsList, ingredientsList);

        Intent intent = new Intent(context, RecipeDisplayActivity.class);
        intent.putExtra("recipe", recipeName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.ingredientsList, pendingIntent);

        Intent onPrev_intent = new Intent(context, GetIngredientListService.class);
        onPrev_intent.setAction(GetIngredientListService.GET_PREVIOUS_INGREDIENT_LIST);
        PendingIntent onPrev_pendingIntent = PendingIntent.getService(context, 0,
                onPrev_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.button_back, onPrev_pendingIntent);

        Intent onNext_intent = new Intent(context, GetIngredientListService.class);
        onNext_intent.setAction(GetIngredientListService.GET_NEXT_INGREDIENT_LIST);
        PendingIntent onNext_pendingIntent = PendingIntent.getService(context, 0,
                onNext_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.button_next, onNext_pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        GetIngredientListService.retrieveIngredientsList(context);
    }

    public static void updateIngredientsList(Context context, AppWidgetManager appWidgetManager,
                                             int[] appWidgetIds, String ingredientsList, String recipeName){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, ingredientsList, recipeName);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

