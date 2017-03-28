package templar.atakr.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import templar.atakr.databaseobjects.Video;

/**
 * Created by Devin on 3/28/2017.
 */

public class VideoUpdate extends IntentService {
    private static final String TAG = VideoUpdate.class.getName();
    public static final String VIDEO_TO_UPDATE = "video_to_update";

    private String mYoutubeVideoId;

    public VideoUpdate(){
        super("VideoUpdate");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        getVideoId(intent);
        DatabaseReference videoReference = getVideoReference();
        doUpdate(videoReference);
    }

    private void getVideoId(Intent intent){
        try{
            mYoutubeVideoId = intent.getStringExtra(VIDEO_TO_UPDATE);
        }catch(NullPointerException e){
            Log.e(TAG, "Can't update video view and popularity, null pointer exception: " + e);
        }
    }

    private DatabaseReference getVideoReference(){
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        return fbdb.getReference().child("Videos").child(mYoutubeVideoId);
    }

    private void doUpdate(DatabaseReference videoReference){
        videoReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Video video = mutableData.getValue(Video.class);
                if(video == null){
                    return Transaction.success(mutableData);
                }
                video.setViews(video.getViews() - 1);
                video.calculatePopularity();

                mutableData.setValue(video);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "videoTransaction:onComplete:" + databaseError);
            }
        });
    }
}
