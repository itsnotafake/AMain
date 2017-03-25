package templar.atakr.design;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.security.InvalidParameterException;

import templar.atakr.R;
import templar.atakr.databaseobjects.Video;
import templar.atakr.framework.MainActivity;
import templar.atakr.framework.VideoPlayActivity;
import templar.atakr.utility.ImageLoaderHelper;
import templar.atakr.youtube.Config;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder>{

    private static final int VIEW_TYPE_NORMAL = 0;
    private final int UNITIALIZED = 10;
    private final int INITIALIZING = 20;
    private final int INITIALIZED = 30;

    private final Context mContext;
    private int mPage;
    private Video mVideo;

    public VideoAdapter(@NonNull Context context, int page){
        mContext = context;
        mPage = page;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        int layoutId = R.layout.video_list_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new VideoAdapterViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(VideoAdapterViewHolder videoAdapterViewHolder, int position){
        //final boolean isExpanded = position==mExpandedPosition;

        switch(mPage){
            case 0:
                mVideo = MainActivity.mTopVideoList.get(position);
                break;
            case 1:
                mVideo = MainActivity.mHotVideoList.get(position);
                break;
            case 2:
                mVideo = MainActivity.mNewVideoList.get(position);
                break;
            default:
                throw new InvalidParameterException("Not a recognized page number, " +
                        "can't access its video list");
        }
        videoAdapterViewHolder.mTitle_TV.setText(mVideo.getYoutubeName());
        videoAdapterViewHolder.mYouTubeThumbnailView.setTag(R.id.videoid, mVideo.getYoutubeVideoId());

        int state = (int) videoAdapterViewHolder.mYouTubeThumbnailView.getTag(R.id.initialize);
        if(state == UNITIALIZED){
            videoAdapterViewHolder.initialize();
        }else if(state == INITIALIZED){
            YouTubeThumbnailLoader loader = (YouTubeThumbnailLoader)
                    videoAdapterViewHolder.mYouTubeThumbnailView.getTag(R.id.thumbnailloader);
            loader.setVideo(mVideo.getYoutubeVideoId());
        }
    }

    @Override
    public int getItemCount(){
        switch(mPage){
            case 0:
                return MainActivity.mTopVideoList.size();
            case 1:
                return MainActivity.mHotVideoList.size();
            case 2:
                return MainActivity.mNewVideoList.size();
            default:
                throw new IllegalArgumentException("Not a recognized page, can't return item count");
        }
    }

    @Override
    public int getItemViewType(int position){
        return VIEW_TYPE_NORMAL;
    }

    class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        YouTubeThumbnailView mYouTubeThumbnailView;
        TextView mTitle_TV;

        VideoAdapterViewHolder(View view, int viewType) {
            super(view);
            mYouTubeThumbnailView = (YouTubeThumbnailView) view.findViewById(R.id.video_list_image);
            mTitle_TV = (TextView) view.findViewById(R.id.video_list_title);

            initialize();
            view.setOnClickListener(this);
        }

        private void initialize(){
            mYouTubeThumbnailView.setTag(R.id.initialize, INITIALIZING);
            mYouTubeThumbnailView.setTag(R.id.thumbnailloader, null);
            mYouTubeThumbnailView.setTag(R.id.videoid, "");

            mYouTubeThumbnailView.initialize(Config.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    mYouTubeThumbnailView.setTag(R.id.initialize, INITIALIZED);
                    mYouTubeThumbnailView.setTag(R.id.thumbnailloader, youTubeThumbnailLoader);

                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                        @Override
                        public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                            youTubeThumbnailLoader.release();
                        }

                        @Override
                        public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                        }
                    });
                    String videoId = (String) mYouTubeThumbnailView.getTag(R.id.videoid);
                    if(videoId != null && !videoId.isEmpty()){
                        youTubeThumbnailLoader.setVideo(videoId);
                    }
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    mYouTubeThumbnailView.setTag(R.id.initialize, UNITIALIZED);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, VideoPlayActivity.class);
            intent.putExtra(VideoPlayActivity.YOUTUBE_VIDEO_ID, mVideo.getYoutubeVideoId());
            mContext.startActivity(intent);
        }
    }
}
