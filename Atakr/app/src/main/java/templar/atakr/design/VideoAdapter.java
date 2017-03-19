package templar.atakr.design;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import templar.atakr.R;
import templar.atakr.framework.VideoBrowseFragment;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_ENLARGED = 1;

    private final Context mContext;
    private Cursor mCursor;

    public VideoAdapter(@NonNull Context context){
        mContext = context;
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
        mCursor.moveToPosition(position);

        String atakrTitle = mCursor.getString(VideoBrowseFragment.INDEX_ATAKR_NAME);
        videoAdapterViewHolder.mTitle_TV.setText(atakrTitle);
    }

    @Override
    public int getItemCount(){
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position){
        return VIEW_TYPE_NORMAL;
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RoundedImageView mRoundedImageView;
        FloatingActionButton mFAB;
        TextView mTitle_TV;

        VideoAdapterViewHolder(View view, int viewType) {
            super(view);

            switch(viewType){
                case VIEW_TYPE_NORMAL:
                    mRoundedImageView = (RoundedImageView) view.findViewById(R.id.video_list_image);
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
