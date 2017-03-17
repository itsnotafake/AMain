package templar.atakr.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
/**
 * Created by Devin on 3/16/2017.
 */

public class VideoSyncIntentService extends IntentService{
    private static final String VIDEO_SYNC_TAG = "atakr-video-sync";

    public static final String INTENT_REQUEST = "bundle_request";
    public static final String INTENT_TITLE = "bundle_title";
    public static final int MAIN_TOP_REQUEST = 100;
    public static final int MAIN_HOT_REQUEST = 110;
    public static final int MAIN_NEW_REQUEST = 120;
    public static final int GAME_REQUEST = 200;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mVideoDatabaseReference;

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
        deleteContents();

        try{
            requestCode = intent.getIntExtra(INTENT_REQUEST, 0);
            title = intent.getStringExtra(INTENT_TITLE);

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

    private void syncMainTop(){
        Query sortedVideoQuery = mVideoDatabaseReference.orderByChild("views").limitToFirst(25);
        //Log.e(VIDEO_SYNC_TAG, sortedVideoQuery.getRef().toString());
        sortedVideoQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot videoSnapshot : dataSnapshot.getChildren()){
                    Log.e(VIDEO_SYNC_TAG, videoSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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

    }
}
