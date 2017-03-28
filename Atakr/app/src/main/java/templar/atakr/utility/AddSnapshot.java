package templar.atakr.utility;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import templar.atakr.databaseobjects.Video;
import templar.atakr.framework.MainActivity;

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
     * @return we return the views value of the final video, this value then updates mTopStartAt
     * value in VideoSyncIntentService
     */
    //return the view value of the item at the 26th position. This value is used to
    //determine where to start our next Firebase database filter query
    public static long addToTopVideoList(DataSnapshot dataSnapshot, int videosToLoad) {
        int counter = 1;
        Video returnVideo = null;

        try{
            for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                if (counter == videosToLoad) {
                    returnVideo = videoSnapshot.getValue(Video.class);;
                    MainActivity.mTopVideoList.add(returnVideo);
                    break;
                } else {
                    Video video = videoSnapshot.getValue(Video.class);
                    MainActivity.mTopVideoList.add(video);
                    counter++;
                }
            }
        }catch(Exception e){
            Log.e(TAG, "Excpetion: " + e);
        }
        try {
            return returnVideo.getViews();
        } catch (NullPointerException e) {
            Log.e(TAG, "[videoSnapshot length < videosToLoad] -> returning 1 from addToTopVideosList");
            return 1;
        }
    }

    public static double addToHotVideoList(DataSnapshot dataSnapshot, int videosToLoad) {
        int counter = 1;
        Video returnVideo = null;

        try {
            for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                if (counter == videosToLoad) {
                    returnVideo = videoSnapshot.getValue(Video.class);
                    break;
                } else {
                    Video video = videoSnapshot.getValue(Video.class);
                    if(video.getPopularity() != -0.0001) {
                        MainActivity.mHotVideoList.add(video);
                    }
                    counter++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
        try {
            returnVideo.calculatePopularity();
            return returnVideo.getPopularity();
        } catch (NullPointerException e) {
            Log.e(TAG, "videoSnapshot length < videosToLoad -> returning 1 from addToHotVideosList");
            return 1;
        }
    }

    public static double addToNewVideoList(DataSnapshot dataSnapshot, int videosToLoad) {
        int counter = 1;
        Video returnVideo = null;

        try {
            for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                if (counter == videosToLoad) {
                    returnVideo = videoSnapshot.getValue(Video.class);
                    break;
                } else {
                    Video video = videoSnapshot.getValue(Video.class);
                    MainActivity.mNewVideoList.add(video);
                    counter++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Excpetion: " + e);
        }
        try {
            return returnVideo.getTimeUploaded();
        } catch (NullPointerException e) {
            Log.e(TAG, "videoSnapshot length < videosToLoad -> returning 0 from addToNewVideosList");
            return -1;
        }
    }
}
