package com.groupe6al2.bubbletalk.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.groupe6al2.bubbletalk.Activity.BubbleActivity;
import com.groupe6al2.bubbletalk.R;


import com.groupe6al2.bubbletalk.Activity.BubbleActivity;
import com.groupe6al2.bubbletalk.R;

public class BubbleOnOff extends AppWidgetProvider {

    private static final String MY_ON_CLICK = "myOnClickTag";
    public static final String STATE_CHANGE = "BubbleOnOff.STATE_CHANGED";
    private static int myAppWidgetId;
    private static boolean isOn = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.i("updateAppWidget", "1");
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        myAppWidgetId = appWidgetId;
        Intent intent = new Intent(context, BubbleOnOff.class);
        intent.putExtra("appWidgetId", appWidgetId);
        intent.setAction(MY_ON_CLICK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bubble_on_off);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        views.setOnClickPendingIntent(R.id.onoffButton, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onReceive", "----------------------------------------------------------------------------1");
        super.onReceive(context, intent);//add this line
        if (STATE_CHANGE.equals(intent.getAction())) {
            isOn =  intent.getExtras().getBoolean("State");

            Log.i("stateOk", "----------------------------------------------------------------------------1");


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bubble_on_off);
            if(isOn){
                Log.i("on-------------", String.valueOf(myAppWidgetId));
                views.setImageViewResource(R.id.onoffButton, R.drawable.on);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(myAppWidgetId, views);
                appWidgetManager.updateAppWidget(appWidgetId, views);
                Intent intentStartApp = new Intent(context.getApplicationContext(), BubbleActivity.class);
                intentStartApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentStartApp);
            }else {
                Log.i("off", "----------------------------------------------------------------------------1");
                views.setImageViewResource(R.id.onoffButton, R.drawable.off);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(myAppWidgetId, views);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

