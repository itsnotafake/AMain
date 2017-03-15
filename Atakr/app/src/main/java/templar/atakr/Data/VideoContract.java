package templar.atakr.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Devin on 3/14/2017.
 */

public class VideoContract {
    //Content authority is name for the entire Content Provider
    static final String CONTENT_AUTHORITY = "templar.atakr";

    //Content authority used to create the base of all URIs
    //Base Uri = "content://templar.atakr"
    public static final Uri BASE_CONTENT_URI = Uri.parse(
            "content://" + CONTENT_AUTHORITY);

    //Path to be used in conjunction with Uri to get Video
    //data
    static final String PATH_VIDEO = "video";

    static final class VideoEntry implements BaseColumns{
        //base CONTENT_URI used to query Video table from the Content Provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                        .buildUpon()
                        .appendPath(PATH_VIDEO)
                        .build();
        //used internally as the name of the video table
        static final String TABLE_NAME = "video";

        public static final String COLUMN_YOUTUBE_VIDEO_ID = "youtube_video_id";
        public static final String COLUMN_YOUTUBE_URL = "youtube_url";
        public static final String COLUMN_YOUTUBE_NAME = "youtube_name";
        public static final String COLUMN_ATAKR_NAME = "atakr_name";
        public static final String COLUMN_UPLOADER = "uploader";
        public static final String COLUMN_VIEWS = "views";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TIME_UPLOADED = "time_uploaded";
    }
}
