package templar.atakr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import templar.atakr.contentprovider.VideoContract;
import templar.atakr.design.VideoAdapter;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoBrowseFragment extends Fragment{
    public static final String ARG_PAGE = "ARG_PAGE";

    //Layout related variables
    private int mPage;
    private RecyclerView mRecyclerView;
    private VideoAdapter mVideoAdapter;

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mVideoAdapter = new VideoAdapter(getContext());
        mRecyclerView.setAdapter(mVideoAdapter);

        return view;
    }
}
