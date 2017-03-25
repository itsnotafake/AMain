package templar.atakr.framework;

import android.os.Bundle;
import android.view.Window;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import templar.atakr.R;
import templar.atakr.youtube.Config;

/**
 * Created by Devin on 3/24/2017.
 */

public class VideoPlayActivity extends YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener{
    public static final String YOUTUBE_VIDEO_ID = "youtube_video_id";
    private static final int ERROR_DIALOG = 1;

    private String mVideoId;
    private YouTubePlayer mYoutubePlayer;
    private YouTubePlayerFragment mYouTubePlayerFragment;
    private MyPlaybackEventListener mMyPlaybackEventListener;
    private MyPlayerStateChangeListener mMyPlayerStateChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_videoplay);

        mVideoId = getIntent().getStringExtra(YOUTUBE_VIDEO_ID);
        mYouTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtubeplayer_fragment);
        mYouTubePlayerFragment.initialize(Config.YOUTUBE_API_KEY, this);
        mMyPlaybackEventListener = new MyPlaybackEventListener();
        mMyPlayerStateChangeListener = new MyPlayerStateChangeListener();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        YouTubePlayer.PlayerStyle style = YouTubePlayer.PlayerStyle.DEFAULT;
        youTubePlayer.setPlayerStyle(style);
        mYoutubePlayer = youTubePlayer;
        mYoutubePlayer.setPlaybackEventListener(mMyPlaybackEventListener);
        mYoutubePlayer.setPlayerStateChangeListener(mMyPlayerStateChangeListener);
        if(!wasRestored){
            youTubePlayer.loadVideo(mVideoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        if(youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(this, ERROR_DIALOG).show();
        }
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener{

        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    }

    private final class MyPlayerStateChangeListener implements
            YouTubePlayer.PlayerStateChangeListener {
        @Override
        public void onAdStarted() {
        }
        @Override
        public void onError(
                com.google.android.youtube.player.YouTubePlayer.ErrorReason arg0) {
        }
        @Override
        public void onLoaded(String arg0) {
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {
            finish();
        }
        @Override
        public void onVideoStarted() {
        }
    }
}
