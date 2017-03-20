package templar.atakr.sync;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import templar.atakr.contentprovider.VideoContract;
import templar.atakr.databaseobjects.Video;
import templar.atakr.utility.AddSnapshot;

/**
 * Created by Devin on 3/16/2017.
 */

public class VideoSyncIntentService extends IntentService{
    private static final String VIDEO_SYNC_TAG = "atakr-video-sync";

    /**
    variables stored in the intent. intent_continuation is a boolean
    that tells us whether or not we are loading the same request type.
    if we are we add to the content provider, if not we delete contents in
    the content provider before adding new content
     */
    public static final String INTENT_REQUEST = "bundle_request";
    public static final String INTENT_TITLE = "bundle_title";
    public static final String INTENT_CONTINUATION = "intent_continuation";

    //constants used to determine what type of request to make to the
    //firebase database
    public static final int MAIN_TOP_REQUEST = 100;
    public static final int MAIN_HOT_REQUEST = 110;
    public static final int MAIN_NEW_REQUEST = 120;
    public static final int GAME_REQUEST = 200;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mVideoDatabaseReference;
    private boolean mContinueLoading;

    //Variables for filtering data from Firebasedatabase
    private static long mTopStartAt;
    private static int mHotStartAt;
    private static int mNewStartAt;

    //Variables for holding the video data synced from FBDB
    public static ArrayList<Video> mTopVideoList = new ArrayList<>();
    public static ArrayList<Video> mHotVideoList = new ArrayList<>();
    public static ArrayList<Video> mNewVideoList = new ArrayList<>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public VideoSyncIntentService() {
        super("VideoSyncIntentService");
    }

    /**
    We empty our content provider, parse the request, and fill up
    our content provider accordingly. As such, the application does
    not cache video information when switching between tabs. This should
    be changed in the future.
    */

    @Override
    protected void onHandleIntent(Intent intent){
        int requestCode;
        String title;
        initializeDatabase();

        try{
            requestCode = intent.getIntExtra(INTENT_REQUEST, 0);
            title = intent.getStringExtra(INTENT_TITLE);
            mContinueLoading = intent.getBooleanExtra(INTENT_CONTINUATION, false);
            if(!mContinueLoading){
                deleteContents();
            }
            switch(requestCode){
                case MAIN_TOP_REQUEST:
                    syncMainTop();
                    return;

                case MAIN_HOT_REQUEST:
                    syncMainHot();
                    return;

                case MAIN_NEW_REQUEST:
                    syncMainNew();
                    return;

                case GAME_REQUEST:
                    syncGame(title);
                    return;

                default:
                    throw new IllegalArgumentException("Not a recognized request Code: " + requestCode);
            }

        }catch(NullPointerException e){
            Log.e(VIDEO_SYNC_TAG, "Null pointer exception: " + e);
        }
    }

    //Load 25 new videos fitting the Top modifier to our content provider
    //The 26th video is used to determine what value to start the next batch at.
    private void syncMainTop(){
        final int videosToLoad = 26;
        Query sortedVideoQuery;

        //If we are adding additional data to the Content Provider we need to use
        //.startAt as part of the query builder, otherwise, if everything has been deleted,
        // we simply start at the beginnning of the Firebase database
        if(mContinueLoading){
            //This query should order videos descending by views,
            // starting at the 26th video from the previous query limited to 26 videos
            sortedVideoQuery = mVideoDatabaseReference
                    .orderByChild("views")
                    .startAt(mTopStartAt, "views")
                    .limitToFirst(videosToLoad);
        }else{
            //This query orders videos descendingly by views, starting at the beginning, limited to 26.
            sortedVideoQuery = mVideoDatabaseReference
                    .orderByChild("views")
                    .limitToFirst(videosToLoad);
        }
        sortedVideoQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTopStartAt = AddSnapshot.doTopSnapshotToCV(
                        dataSnapshot,
                        videosToLoad,
                        getApplicationContext()
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void syncMainHot(){

    }

    private void syncMainNew(){

    }

    private void syncGame(String game){

    }

    public void initializeDatabase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mVideoDatabaseReference = mFirebaseDatabase.getReference().child("Videos");
    }

    private void deleteContents(){
        mTopStartAt = 0;
        mHotStartAt = 0;
        mNewStartAt = 0;

        //First check to see if the database is already empty
        String[] projectionColumns = {VideoContract.VideoEntry._ID};
        Cursor cursor = getApplicationContext().getContentResolver().query(
                VideoContract.VideoEntry.CONTENT_URI,
                projectionColumns,
                null,
                null,
                null
        );

        if(!(null == cursor || cursor.getCount() == 0)){
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            contentResolver.delete(
                    VideoContract.VideoEntry.CONTENT_URI,
                    null,
                    null
            );
            cursor.close();
        }
    }
}
