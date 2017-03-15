package templar.atakr.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Devin on 3/14/2017.
 */

public class VideoDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "video.db";
    private static final int DATABASE_VERSION = 3;

    public VideoDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_VIDEO_TABLE =
                "CREATE TABLE " + VideoContract.VideoEntry.TABLE_NAME + " (" +
                        VideoContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        VideoContract.VideoEntry.COLUMN_YOUTUBE_VIDEO_ID + " TEXT NOT NULL, " +
                        VideoContract.VideoEntry.COLUMN_YOUTUBE_URL + " TEXT NOT NULL, " +
                        VideoContract.VideoEntry.COLUMN_YOUTUBE_NAME + " TEXT NOT NULL, " +
                        VideoContract.VideoEntry.COLUMN_ATAKR_NAME + " TEXT, " +
                        VideoContract.VideoEntry.COLUMN_UPLOADER + " TEXT NOT NULL, " +
                        VideoContract.VideoEntry.COLUMN_VIEWS + " INTEGER NOT NULL, " +
                        VideoContract.VideoEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, " +
                        VideoContract.VideoEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                        VideoContract.VideoEntry.COLUMN_TIME_UPLOADED + " INTEGER NOT NULL, " +
                        " UNIQUE (" + VideoContract.VideoEntry.COLUMN_YOUTUBE_VIDEO_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoContract.VideoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
