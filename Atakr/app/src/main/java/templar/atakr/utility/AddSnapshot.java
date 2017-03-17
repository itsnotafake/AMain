package templar.atakr.utility;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.collect.Iterables;
import com.google.firebase.database.DataSnapshot;

import templar.atakr.contentprovider.VideoContract;
import templar.atakr.databaseobjects.Video;

/**
 * Created by Devin on 3/17/2017.
 */

public class AddSnapshot {
    private static final String TAG = AddSnapshot.class.getSimpleName();

    public AddSnapshot(){

    }

    /**
     *
     * @param dataSnapshot a handle on the sequence of video objects we have loaded from the
     *                     Firebase Database
     * @param videosToLoad the number of videos we are loading into our content provider. The
     *                     actual number is videosToLoad - 1, because the last video's view value
     *                     is used to determine where to start our next Firebase Database query.
     * @param context Context necessary for getting a handle on the content resolver needed to
     *                insert values into the content provider
     * @return we return the views value of the final video, this value then updates mTopStartAt
     * value in VideoSyncIntentService
     */
    //return the view value of the item at the 26th position. This value is used to
    //determine where to start our next Firebase database filter query
    public static long doTopSnapshotToCV(DataSnapshot dataSnapshot, int videosToLoad, Context context){
        int counter = 1;
        DataSnapshot returnSnapshot = null;
        ContentValues[] videos =
                new ContentValues[Iterables.size(dataSnapshot.getChildren())];

        try {
            for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                if (counter == videosToLoad) {
                    returnSnapshot = videoSnapshot;
                    break;
                } else {
                    Video video = videoSnapshot.getValue(Video.class);
                    ContentValues videoCV = new ContentValues();

                    videoCV.put(VideoContract.VideoEntry.COLUMN_YOUTUBE_VIDEO_ID, video.getYoutubeVideoId());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_YOUTUBE_URL, video.getYoutubeUrl());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_YOUTUBE_NAME, video.getYoutubeName());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_ATAKR_NAME, video.getAtakrName());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_UPLOADER, video.getUploader());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_VIEWS, video.getViews());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_THUMBNAIL_URL, video.getYoutubeThumbailUrl());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_POPULARITY, video.getPopularity());
                    videoCV.put(VideoContract.VideoEntry.COLUMN_TIME_UPLOADED, video.getYoutubeVideoId());

                    videos[counter - 1] = videoCV;
                    counter++;
                }
            }
            ContentResolver videoContentResolver = context.getContentResolver();
            videoContentResolver.bulkInsert(
                    VideoContract.VideoEntry.CONTENT_URI,
                    videos
            );
        }catch(Exception e){
            Log.e(TAG, "Excpetion: " + e);
        }
        try{
            return returnSnapshot.getValue(Video.class).getViews();
        }catch(NullPointerException e){
            return 0;
        }
    }
}
