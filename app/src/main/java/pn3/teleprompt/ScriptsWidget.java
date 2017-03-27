package pn3.teleprompt;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ScriptsWidget extends AppWidgetProvider {


    public static String EXTRA_WORD=
            "pn3.Teleprompt.WORD";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.e("onUpdate()",appWidgetIds.toString());
        for (int appWidgetId : appWidgetIds) {
            Intent svcIntent=new Intent(context,WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri((Intent.URI_INTENT_SCHEME))));

            RemoteViews widget=new RemoteViews(context.getPackageName(),R.layout.scripts_widget);
            //widget.setRemoteAdapter(R.id.list,svcIntent);
            widget.setRemoteAdapter(appWidgetId,R.id.list,svcIntent);

            Intent i=new Intent(context,MainActivity.class);
            PendingIntent pi=PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.list,pi);

            //widget.setOnClickPendingIntent(R.id.list,pi);
            appWidgetManager.updateAppWidget(appWidgetId,widget);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
        Log.e("Recived","c");
    }
}

