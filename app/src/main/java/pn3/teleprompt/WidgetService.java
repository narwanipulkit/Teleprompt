package pn3.teleprompt;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by pulkitnarwani on 23/03/17.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        TelepromptViewFactory tvf=new TelepromptViewFactory(getBaseContext(),intent);
        Log.e("abc",String.valueOf(tvf.getViewAt(0).getLayoutId()));
        return tvf;

    }
}
