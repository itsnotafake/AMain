package templar.atakr.YouTube;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;

import java.io.IOException;
import java.util.List;

/**
 * Created by Devin on 3/10/2017.
 */

    public class YoutubeData {
    private static final String TAG = YoutubeData.class.getSimpleName();

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static YouTube mYoutube;

    public static final String VIDEO_TITLE = "video_title";
    public static final String VIDEO_UPLOADER = "video_uploader";
    public static final String VIDEO_VIEWS = "video_views";
    public static final String VIDEO_THUMBNAIL = "video_thumbnail";

    public static Bundle getVideoValues(String videoId){
        Bundle bundle = null;
        mYoutube = new YouTube.Builder(HTTP_TRANSPORT,
               JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException{}
        }).setApplicationName("templar.atakr").build();

        try {
            //Get youtube video from videoId
            Log.e(TAG, videoId);
            YouTube.Videos.List videoRequest = mYoutube.videos().list("snippet,statistics");
            videoRequest.setId(videoId);
            videoRequest.setKey(Config.YOUTUBE_API_KEY);
            VideoListResponse videoListResponse = videoRequest.execute();
            List<Video> videoList = videoListResponse.getItems();
            if(videoList.isEmpty()){
                Log.e(TAG, "Can't find a video with ID: " + videoId);
                return null;
            }

            //Create Bundle and get relevant information
            bundle = new Bundle();
            Video video = videoList.get(0);
            VideoSnippet vSn = video.getSnippet();
            VideoStatistics vSt = video.getStatistics();
            String title = vSn.getTitle();
            ThumbnailDetails thumbnails = vSn.getThumbnails();
            Thumbnail thumbnail = thumbnails.getDefault();
            String uploader = vSn.getChannelTitle();
            String views = vSt.getViewCount().toString();

            bundle.putString(VIDEO_TITLE, title);
            bundle.putString(VIDEO_UPLOADER, uploader);
            bundle.putString(VIDEO_VIEWS, views);
            bundle.putString(VIDEO_THUMBNAIL, thumbnail.getUrl());
        }catch(IOException e){
            Log.e(TAG, "Error " + e);
        }
        return bundle;
    }
}
