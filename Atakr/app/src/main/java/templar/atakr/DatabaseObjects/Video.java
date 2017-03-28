package templar.atakr.databaseobjects;

import android.util.Log;

import templar.atakr.utility.Conversions;

/**
 * Created by Devin on 3/9/2017.
 */

public class Video {
    private static final String TAG = Video.class.getSimpleName();

    /**
     * Popularity,views, and timeUploaded are all stored as negative numbers so that Firebase Database
     * can retrieve videos sorted descendingly by those categories(top,trending,new). (Currently,
     * Firebases only retrieval in sorted order is done ascendingly).
     */
    private String youtubeVideoId;
    private String youtubeUrl;
    private String youtubeName;
    private String atakrName;
    private String uploader;
    //private String creator;
    private long views;
    private String youtubeThumbailUrl;
    private double popularity; // views / (current time - time uploaded) = views per time unit (in ms)
    private final double timeUploaded = Conversions.getNegTimeUploaded(System.currentTimeMillis());
    private double timeSinceUpload;

    public Video(){
    }

    public Video(String id, String url, String yName, String aName, String uploadedBy, String yUrl, String v){
        youtubeVideoId = id;
        youtubeUrl = url;
        youtubeName = yName;
        atakrName = aName;
        uploader = uploadedBy;
        youtubeThumbailUrl = yUrl;
        views = Long.parseLong(v);
        timeSinceUpload = (System.currentTimeMillis() - Conversions.getPosTimeUploaded(timeUploaded))/1000;
    }

    public void calculatePopularity(){
        updateTimeSinceLoaded();

        if(timeSinceUpload>0) {
            double v = views*100;
            popularity = v/timeSinceUpload;
        }else{
            popularity = -0.0001;
        }
    }

    private void updateTimeSinceLoaded(){
        timeSinceUpload = (System.currentTimeMillis() - Conversions.getPosTimeUploaded(timeUploaded))/1000;
    }

    public String getYoutubeVideoId(){ return youtubeVideoId;}
    public void setYoutubeVideoId(String s){ youtubeVideoId = s;}

    public String getYoutubeUrl(){return youtubeUrl;}
    public void setYoutubeUrl(String s){
        youtubeUrl = s;}

    public String getYoutubeName(){return youtubeName;}
    public void setYoutubeName(String s){youtubeName = s;}

    public String getAtakrName(){return atakrName;}
    public void setAtakrName(String s){atakrName = s;}

    public String getUploader(){return uploader;}
    public void setUploader(String s){uploader = s;}

    public long getViews(){return views;}
    public void setViews(long l){views = l;}

    public String getYoutubeThumbailUrl(){return youtubeThumbailUrl;}
    public void setYoutubeThumbailUrl(String yUrl){youtubeThumbailUrl = yUrl;}

    public double getPopularity(){return popularity;}
    public void setPopularity(double f){popularity = f;}

    public double getTimeUploaded(){return timeUploaded;}

    public String toString(){
        return youtubeName;
    }
}
