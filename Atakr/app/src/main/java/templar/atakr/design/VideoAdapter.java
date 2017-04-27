package templar.atakr.design;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.security.InvalidParameterException;

import templar.atakr.R;
import templar.atakr.databaseobjects.Video;
import templar.atakr.framework.MainActivity;
import templar.atakr.framework.SearchActivity;
import templar.atakr.framework.VideoPlayActivity;
import templar.atakr.sync.VideoUpdate;
import templar.atakr.utility.ImageLoaderHelper;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder>{
    private static final String TAG = VideoAdapter.class.getName();
    private static final int VIEW_TYPE_NORMAL = 0;
    private final Context mContext;
    private int mPage;

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
    public void onBindViewHolder(final VideoAdapterViewHolder videoAdapterViewHolder, int position){
        videoAdapterViewHolder.mListItemExtended.setVisibility(View.GONE);
        videoAdapterViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                videoAdapterViewHolder.mListItemExtended.setVisibility(
                        videoAdapterViewHolder.mListItemExtended.getVisibility() == View.VISIBLE
                                ? View.GONE : View.VISIBLE);
                return true;
            }
        });
        switch(mPage){
            case 0:
                videoAdapterViewHolder.setVideo(MainActivity.mTopVideoList.get(position));
                break;
            case 1:
                videoAdapterViewHolder.setVideo(MainActivity.mHotVideoList.get(position));
                break;
            case 2:
                videoAdapterViewHolder.setVideo(MainActivity.mNewVideoList.get(position));
                break;
            case 3:
                videoAdapterViewHolder.setVideo(SearchActivity.mSearchList.get(position));
            default:
                throw new InvalidParameterException("Not a recognized page number, " +
                        "can't access its video list");
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
            case 3:
                return SearchActivity.mSearchList.size();
            default:
                throw new IllegalArgumentException("Not a recognized page, can't return item count");
        }
    }

    @Override
    public int getItemViewType(int position){
        return VIEW_TYPE_NORMAL;
    }

    class VideoAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        Video mVideo;
        NetworkImageView mNetworkImageView;
        TextView mTitle_TV;

        View mView;
        RelativeLayout mListItemExtended;

        VideoAdapterViewHolder(View view, int viewType) {
            super(view);
            mTitle_TV = (TextView) view.findViewById(R.id.video_list_title);
            mNetworkImageView = (NetworkImageView) view.findViewById(R.id.video_list_image);

            mView = view;
            mListItemExtended = (RelativeLayout) view.findViewById(R.id.list_item_extended);

            view.setOnClickListener(this);
        }

        public void setVideo(Video video) {
            mVideo = video;
            if (mVideo.getAtakrName() == null || mVideo.getAtakrName().equals("")) {
                mTitle_TV.setText(mVideo.getYoutubeName());
            } else {
                mTitle_TV.setText(mVideo.getAtakrName());
            }
            mNetworkImageView.setImageUrl(
                    mVideo.getYoutubeThumbailUrl(),
                    ImageLoaderHelper.getInstance(mContext).getImageLoader()
            );
        }

        @Override
        public void onClick(View v) {
            //This intent service handles updating view count and popularity.
            //This happens off the UI thread because of the intent service
            Intent videoUpdate = new Intent(mContext, VideoUpdate.class);
            videoUpdate.putExtra(
                    VideoUpdate.VIDEO_TO_UPDATE,
                    mVideo.getYoutubeVideoId());
            mContext.startService(videoUpdate);

            //This intent starts the video play activity
            Intent videoPlay = new Intent(mContext, VideoPlayActivity.class);
            videoPlay.putExtra(VideoPlayActivity.YOUTUBE_VIDEO_ID, mVideo.getYoutubeVideoId());
            mContext.startActivity(videoPlay);
        }
    }
}
