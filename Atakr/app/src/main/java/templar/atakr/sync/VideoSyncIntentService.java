package templar.atakr.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import templar.atakr.framework.MainActivity;
import templar.atakr.framework.VideoBrowseFragment;
import templar.atakr.utility.AddSnapshot;

/**
 * Created by Devin on 3/16/2017.
 */

public class VideoSyncIntentService extends IntentService {
    private static final String VIDEO_SYNC_TAG = "atakr-video-sync";
    private static final int VIDEOS_TO_LOAD = 15;
    //different value so we avoid videos not being shown if they have
    //equivalent views value as video at the end of last bucket
    private static final int TOP_VIDEOS_TO_LOAD = VIDEOS_TO_LOAD * 4;

    /**
     * variables stored in the intent. intent_continuation is a boolean
     * that tells us whether or not we are loading the same request type.
     * if we are we add to the content provider, if not we delete contents in
     * the content provider before adding new content
     */
    public static final String INTENT_REQUEST = "bundle_request";
    public static final String INTENT_TITLE = "bundle_title";
    public static final String INTENT_DELETE = "intent_delete";
    public static final String INTENT_INIT_DB = "intent_init_db";

    //constants used to determine what type of request to make to the
    //firebase database
    public static final int NO_REQUEST = 100;
    public static final int TOP_REQUEST = 210;
    public static final int HOT_REQUEST = 220;
    public static final int NEW_REQUEST = 230;
    public static final int GAME_REQUEST = 310;
    public static final int ALL_REQUEST_MG = 410; //minus game

    public static final int NO_DELETE = 1000;
    public static final int TOP_DELETE = 2100;
    public static final int HOT_DELETE = 2200;
    public static final int NEW_DELETE = 2300;
    public static final int GAME_DELETE = 3100;
    public static final int ALL_DELETE = 4100;

    private FirebaseDatabase mFirebaseDatabase;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public VideoSyncIntentService() {
        super("VideoSyncIntentService");
    }

    /**
     * We empty our content provider, parse the request, and fill up
     * our content provider accordingly. As such, the application does
     * not cache video information when switching between tabs. This should
     * be changed in the future.
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        int requestCode;
        int deleteCode;

        try {
            requestCode = intent.getIntExtra(INTENT_REQUEST, 0);
            deleteCode = intent.getIntExtra(INTENT_DELETE, 0);
            String gameTitle = intent.getStringExtra(INTENT_TITLE);
            boolean initDB = intent.getBooleanExtra(INTENT_INIT_DB, false);
            if(initDB){
                initializeDatabase(requestCode);
            }

            deleteContents(deleteCode);

            switch (requestCode) {
                case NO_REQUEST:
                    return;
                case TOP_REQUEST:
                    syncMainTop();
                    return;
                case HOT_REQUEST:
                    syncMainHot();
                    return;
                case NEW_REQUEST:
                    syncMainNew();
                    return;
                case GAME_REQUEST:
                    syncGame(gameTitle);
                    return;
                case ALL_REQUEST_MG:
                    syncMainTop();
                    syncMainHot();
                    syncMainNew();
                    return;
                default:
                    throw new IllegalArgumentException(
                            "Not a recognized request Code: " + requestCode + ", no videos synced");
            }

        } catch (NullPointerException e) {
            Log.e(VIDEO_SYNC_TAG, "Null pointer exception: " + e);
        }
    }

    //Load 25 new videos fitting the Top modifier to our content provider
    //The 26th video is used to determine what value to start the next batch at.
    private void syncMainTop() {
        Query sortedVideoQuery;

        //If we are adding additional data to the Content Provider we need to use
        //.startAt as part of the query builder, otherwise, if everything has been deleted,
        // we simply start at the beginnning of the Firebase database
        if (MainActivity.mStartTopQueryAt != 0) {
            /*
            This query should order videos descending by views,
            starting at the VIDEOS_TO_LOAD video from the previous query limited to VIDEOS_TO_LOAD
             */
            sortedVideoQuery = MainActivity.mTopVideoDatabaseReference
                    .orderByChild("views")
                    .startAt(MainActivity.mStartTopQueryAt, "youtubeVideoId")
                    .limitToFirst(TOP_VIDEOS_TO_LOAD);
        } else {
            //This query orders videos descendingly by views, starting at the beginning,
            //limited to VIDEOS_TO_LOAD
            sortedVideoQuery = MainActivity.mTopVideoDatabaseReference
                    .orderByChild("views")
                    .limitToFirst(TOP_VIDEOS_TO_LOAD);
        }
        sortedVideoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.mStartTopQueryAt = AddSnapshot.addToTopVideoList(
                        dataSnapshot,
                        TOP_VIDEOS_TO_LOAD
                );
                Intent intent = new Intent(VideoBrowseFragment.VIDEO_DATA_BROADCAST + 0);
                Log.e(VIDEO_SYNC_TAG, "Sending top broadcast");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void syncMainHot() {
        Query sortedVideoQuery;
        double lastThreeDaysMilli = (System.currentTimeMillis() - 2.592e+8)*-1;

        //If we are adding additional data to the Content Provider we need to use
        //.startAt as part of the query builder, otherwise, if everything has been deleted,
        // we simply start at the beginnning of the Firebase database
        if (MainActivity.mStartHotQueryAt != 0) {
            //This query should order videos descending by views,
            // starting at the 26th video from the previous query limited to 26 videos
            sortedVideoQuery = MainActivity.mHotVideoDatabaseReference
                    .orderByChild("popularity")
                    .startAt(MainActivity.mStartHotQueryAt, "popularity")
                    .limitToFirst(VIDEOS_TO_LOAD);
        } else {
            //This query orders videos descendingly by views, starting at the beginning, limited to 26.
            sortedVideoQuery = MainActivity.mHotVideoDatabaseReference
                    .orderByChild("popularity")
                    .limitToFirst(VIDEOS_TO_LOAD);
        }
        sortedVideoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.mStartHotQueryAt = AddSnapshot.addToHotVideoList(
                        dataSnapshot,
                        VIDEOS_TO_LOAD
                );
                Log.e(VIDEO_SYNC_TAG, "Size is: " + MainActivity.mHotVideoList.size());
                Intent intent = new Intent(VideoBrowseFragment.VIDEO_DATA_BROADCAST + 1);
                Log.e(VIDEO_SYNC_TAG, "Sending hot broadcast");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void syncMainNew() {
        Query sortedVideoQuery;

        //If we are adding additional data to the Content Provider we need to use
        //.startAt as part of the query builder, otherwise, if everything has been deleted,
        // we simply start at the beginnning of the Firebase database
        if (MainActivity.mStartNewQueryAt != 0) {
            //This query should order videos descending by views,
            // starting at the 26th video from the previous query limited to 26 videos
            sortedVideoQuery = MainActivity.mNewVideoDatabaseReference
                    .orderByChild("timeUploaded")
                    .startAt(MainActivity.mStartNewQueryAt, "timeUploaded")
                    .limitToFirst(VIDEOS_TO_LOAD);
        } else {
            //This query orders videos descendingly by views, starting at the beginning, limited to 26.
            sortedVideoQuery = MainActivity.mNewVideoDatabaseReference
                    .orderByChild("timeUploaded")
                    .limitToFirst(VIDEOS_TO_LOAD);
        }
        sortedVideoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.mStartNewQueryAt = AddSnapshot.addToNewVideoList(
                        dataSnapshot,
                        VIDEOS_TO_LOAD
                );
                Intent intent = new Intent(VideoBrowseFragment.VIDEO_DATA_BROADCAST + 2);
                Log.e(VIDEO_SYNC_TAG, "Sending new broadcast");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void syncGame(String game) {

    }

    public void initializeDatabase(int requestCode) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        switch(requestCode){
            case TOP_REQUEST:
                MainActivity.mTopVideoDatabaseReference =
                        mFirebaseDatabase.getReference().child("Videos");
                return;
            case HOT_REQUEST:
                MainActivity.mHotVideoDatabaseReference =
                        mFirebaseDatabase.getReference().child("Videos");
                return;
            case NEW_REQUEST:
                MainActivity.mNewVideoDatabaseReference =
                        mFirebaseDatabase.getReference().child("Videos");
                return;
            case GAME_REQUEST:
                break;
            case ALL_REQUEST_MG:
                initializeDatabase(TOP_REQUEST);
                initializeDatabase(HOT_REQUEST);
                initializeDatabase(NEW_REQUEST);
                return;
            default:
                throw new IllegalArgumentException(
                        "Cannot initialize Database, invalid requestCode: " + requestCode);
        }
    }

    private void deleteContents(int deleteCode) {
        switch (deleteCode) {
            case NO_DELETE:
                return;
            case TOP_DELETE:
                MainActivity.mStartTopQueryAt = 0;
                MainActivity.mTopVideoList.clear();
                return;
            case HOT_DELETE:
                MainActivity.mStartHotQueryAt = 0;
                MainActivity.mHotVideoList.clear();
                return;
            case NEW_DELETE:
                MainActivity.mStartNewQueryAt = 0;
                MainActivity.mNewVideoList.clear();
                return;
            case GAME_DELETE:
                //MainActivity.mStartGameQueryAt = ?
                MainActivity.mGameVideoList.clear();
                return;
            case ALL_DELETE:
                deleteContents(TOP_DELETE);
                deleteContents(HOT_DELETE);
                deleteContents(NEW_DELETE);
                deleteContents(GAME_DELETE);
                return;
            default:
                Log.e(VIDEO_SYNC_TAG, "Not a recognized deleteContent code: "
                        + deleteCode + ", nothing deleted");
        }
    }
}
