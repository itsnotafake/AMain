package templar.atakr.design;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.security.InvalidParameterException;

import templar.atakr.R;
import templar.atakr.databaseobjects.Video;
import templar.atakr.framework.MainActivity;
import templar.atakr.utility.ImageLoaderHelper;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder>{

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_ENLARGED = 1;

    private final Context mContext;
    private int mPage;

    public VideoAdapter(@NonNull Context context, int page){
        mContext = context;
        mPage = page;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        int layoutId;

        switch(viewType) {
            case VIEW_TYPE_NORMAL:
                layoutId = R.layout.video_list_item_normal;
                break;
            case VIEW_TYPE_ENLARGED:
                layoutId = R.layout.video_list_item_enlarged;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new VideoAdapterViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(VideoAdapterViewHolder videoAdapterViewHolder, int position){
        Video video;
        switch(mPage){
            case 0:
                video = MainActivity.mTopVideoList.get(position);
                break;
            case 1:
                video = MainActivity.mHotVideoList.get(position);
                break;
            case 2:
                video = MainActivity.mNewVideoList.get(position);
                break;
            default:
                throw new InvalidParameterException("Not a recognized page number, can't access its video list");
        }
        videoAdapterViewHolder.mTitle_TV.setText(video.getYoutubeName());
        videoAdapterViewHolder.mNetworkImageView.setImageUrl(
                video.getYoutubeThumbailUrl(),
                ImageLoaderHelper.getInstance(mContext).getImageLoader()
        );
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
        NetworkImageView mNetworkImageView;
        FloatingActionButton mFAB;
        TextView mTitle_TV;

        VideoAdapterViewHolder(View view, int viewType) {
            super(view);

            switch(viewType){
                case VIEW_TYPE_NORMAL:
                    mNetworkImageView = (NetworkImageView) view.findViewById(R.id.video_list_image);
                    mFAB = (FloatingActionButton) view.findViewById(R.id.video_list_play);
                    mTitle_TV = (TextView) view.findViewById(R.id.video_list_title);
                    break;

                case VIEW_TYPE_ENLARGED:
                    break;

                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
