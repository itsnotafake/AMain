package templar.atakr.framework;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import templar.atakr.R;
import templar.atakr.databaseobjects.Video;
import templar.atakr.sync.VideoSyncIntentService;

/**
 * Created by Devin on 3/30/2017.
 */

public class AtakrWidgetProvider extends AppWidgetProvider {
    public static DatabaseReference mVideoReference;

    private RemoteViews mViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for(int appWidgetId : appWidgetIds){
            mViews = new RemoteViews(context.getPackageName(), R.layout.atakr_widget);
            doSync(context);

            appWidgetManager.updateAppWidget(appWidgetId, mViews);
        }
    }

    private void doSync(Context context){
        Intent intent = new Intent(context, VideoSyncIntentService.class);
        intent.putExtra(VideoSyncIntentService.INTENT_REQUEST, VideoSyncIntentService.WIDGET_REQUEST);
        intent.putExtra(VideoSyncIntentService.INTENT_DELETE, VideoSyncIntentService.NO_DELETE);
        intent.putExtra(VideoSyncIntentService.INTENT_INIT_DB, true);
        intent.putExtra(VideoSyncIntentService.INTENT_TITLE, "");
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mViews.setOnClickPendingIntent(R.id.widget_play_button, pendingIntent);
        mViews.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
    }
}
