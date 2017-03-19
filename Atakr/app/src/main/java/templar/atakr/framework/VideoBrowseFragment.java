package templar.atakr.framework;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;

import templar.atakr.R;
import templar.atakr.contentprovider.VideoContract;
import templar.atakr.design.VideoAdapter;
import templar.atakr.sync.VideoSyncIntentService;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoBrowseFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = VideoBrowseFragment.class.getName();
    public static final String ARG_PAGE = "ARG_PAGE";

    //Layout related variables
    private int mPage;
    private RecyclerView mRecyclerView;
    private VideoAdapter mVideoAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    //Variables for dealing with our Video Content Provider
    private static final int ID_VIDEO_LOADER = 69;
    public static final String[] MAIN_VIDEO_PROJECTION = {
            VideoContract.VideoEntry.COLUMN_YOUTUBE_NAME,
            VideoContract.VideoEntry.COLUMN_ATAKR_NAME,
            VideoContract.VideoEntry.COLUMN_UPLOADER,
            VideoContract.VideoEntry.COLUMN_VIEWS,
            VideoContract.VideoEntry.COLUMN_THUMBNAIL_URL,
            VideoContract.VideoEntry.COLUMN_YOUTUBE_URL,
    };
    public static final int INDEX_YOUTUBE_NAME = 0;
    public static final int INDEX_ATAKR_NAME = 1;
    public static final int INDEX_UPLOADER = 2;
    public static final int INDEX_VIEWS = 3;
    public static final int INDEX_THUMBNAIL_URL = 4;
    public static final int INDEX_YOUTUBE_URL = 5;

    public static VideoBrowseFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        VideoBrowseFragment fragment = new VideoBrowseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_videobrowse, container, false);

        //Set up recycler view
        //initializeRecyclerView();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mVideoAdapter = new VideoAdapter(getContext());
        mRecyclerView.setAdapter(mVideoAdapter);

        getActivity().getSupportLoaderManager().initLoader(ID_VIDEO_LOADER, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle){
        switch (loaderId) {
            case ID_VIDEO_LOADER:
                Uri videoQueryUri = VideoContract.VideoEntry.CONTENT_URI;
                String sortOrder = VideoContract.VideoEntry.COLUMN_VIEWS + " DESC";

                return new CursorLoader(
                        this.getContext(),
                        videoQueryUri,
                        MAIN_VIDEO_PROJECTION,
                        null,
                        null,
                        sortOrder
                );

            default:
                throw new RuntimeException("Loader not Implemented" + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        if(data == null){
            Log.e(TAG, "cursor is null");
            return;
        }
        mVideoAdapter.swapCursor(data);
        if(mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if(data.getCount() != 0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mVideoAdapter.swapCursor(null);
    }
}
