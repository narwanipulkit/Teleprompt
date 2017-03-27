package pn3.teleprompt;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by pulkitnarwani on 24/03/17.
 */

public class TelepromptViewFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Intent intent;
    int appWidgetId;
    Cursor c;

    TelepromptViewFactory(Context c,Intent i){
        mContext=c;
        intent=i;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    @Override
    public void onCreate() {

        String url="content://DataProvider/data";
        c=mContext.getApplicationContext().getContentResolver().query(Uri.parse(url),null,null,null,"_id");
        c.moveToFirst();

    }

    @Override
    public void onDataSetChanged() {


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return c.getCount()+1;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        String title=new String();
        Log.e("getViewAt","c");
        RemoteViews rv;
        if(i==0){
            rv=new RemoteViews(mContext.getPackageName(),R.layout.widget_item);
            rv.setInt(R.id.widget_title,"setBackgroundResource",android.R.color.holo_blue_dark);
            rv.setTextViewTextSize(R.id.widget_title, TypedValue.COMPLEX_UNIT_DIP,30);
            rv.setTextViewText(R.id.widget_title," Teleprompt Scripts");
            rv.setTextColor(R.id.widget_title,mContext.getResources().getColor(android.R.color.white));

        }
        else {
            if (c != null && i > 0 && c.getCount() > 0) {
                c.moveToPosition(i-1);
                title = c.getString(1);
            }

            rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            rv.setTextViewText(R.id.widget_title," "+ title);
            rv.setTextColor(R.id.widget_title,mContext.getResources().getColor(android.R.color.black));
            rv.setTextViewTextSize(R.id.widget_title, TypedValue.COMPLEX_UNIT_DIP, 25);
            Log.e("Text",title);
            Intent in = new Intent();
            Bundle extras = new Bundle();

            extras.putString(ScriptsWidget.EXTRA_WORD, String.valueOf(i));
            in.putExtras(extras);
            rv.setOnClickFillInIntent(android.R.id.list, in);
            Log.e(String.valueOf(R.layout.item_scripts), String.valueOf(rv.getLayoutId()));
        }
        return rv;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
