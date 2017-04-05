package templar.atakr.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import templar.atakr.R;
import templar.atakr.databaseobjects.Video;
import templar.atakr.design.EndlessRecyclerViewScrollListener;
import templar.atakr.design.VideoAdapter;
import templar.atakr.sync.VideoSyncIntentService;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoBrowseFragment extends Fragment{
    private static final String TAG = VideoBrowseFragment.class.getName();
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String VIDEO_DATA_BROADCAST = "myVideoDataBroadcastListener";

    //Layout related variables
    private int mPage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private CustomGridLayoutManager mLayoutManager;
    private VideoAdapter mVideoAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ProgressBar mProgressBar;
    private int mPosition = RecyclerView.NO_POSITION;

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

        if(savedInstanceState == null){
            initializeBroadcastReceiver();
        }
        mVideoAdapter = new VideoAdapter(getContext(), mPage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_videobrowse, container, false);

        //Set up recycler view
        initializeRecyclerView(view);
        //Assign progress bar
        initializeProgressBar(view);
        //Initialize Swipe Refresh
        initializeSwipeRefresh(view);
        return view;
    }

    public void initializeBroadcastReceiver(){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "Broadcast received");
                mProgressBar.setVisibility(View.GONE);
                mVideoAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                mLayoutManager.setScrollEnabled(true);
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiver, new IntentFilter(VIDEO_DATA_BROADCAST + mPage));
    }

    public void initializeRecyclerView(View view){
        final int columns = getResources().getInteger(R.integer.main_list_columns);
        mLayoutManager = new CustomGridLayoutManager(getContext(), columns);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mVideoAdapter);
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                boolean loadMore = true;
                Intent intent = new Intent(getContext(), VideoSyncIntentService.class);
                intent.putExtra(
                        VideoSyncIntentService.INTENT_DELETE,
                        VideoSyncIntentService.NO_DELETE);
                intent.putExtra(VideoSyncIntentService.INTENT_TITLE, "");
                intent.putExtra(VideoSyncIntentService.INTENT_INIT_DB, false);
                switch(mPage){
                    case 0:
                        if(MainActivity.mStartTopQueryAt == 1){
                            loadMore = false;
                        }
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_REQUEST,
                                VideoSyncIntentService.TOP_REQUEST);
                        break;
                    case 1:
                        if(MainActivity.mStartHotQueryAt == 1){
                            loadMore = false;
                        }
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_REQUEST,
                                VideoSyncIntentService.HOT_REQUEST);
                        break;
                    case 2:
                        if(MainActivity.mStartNewQueryAt == -1){
                            loadMore = false;
                        }
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_REQUEST,
                                VideoSyncIntentService.NEW_REQUEST);
                        break;
                    default:
                        throw new IllegalArgumentException
                                ("Illegal argument for onLoadMore, bad Page: " + mPage);
                }
                if(loadMore) {
                    getContext().startService(intent);
                }
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    public void initializeProgressBar(View view){
        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_browse_progress);
        switch(mPage){
            case 0:
                if(MainActivity.mTopVideoList.isEmpty()){
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if(MainActivity.mHotVideoList.isEmpty()){
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if(MainActivity.mNewVideoList.isEmpty()){
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    public void initializeSwipeRefresh(View view){
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_browse_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLayoutManager.setScrollEnabled(false);
                Intent intent = new Intent(getContext(), VideoSyncIntentService.class);
                intent.putExtra(VideoSyncIntentService.INTENT_TITLE, "");
                intent.putExtra(VideoSyncIntentService.INTENT_INIT_DB, true);
                switch(mPage){
                    case 0:
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_REQUEST,
                                VideoSyncIntentService.TOP_REQUEST);
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_DELETE,
                                VideoSyncIntentService.TOP_DELETE);
                        break;
                    case 1:
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_REQUEST,
                                VideoSyncIntentService.HOT_REQUEST);
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_DELETE,
                                VideoSyncIntentService.HOT_DELETE);
                        break;
                    case 2:
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_REQUEST,
                                VideoSyncIntentService.NEW_REQUEST);
                        intent.putExtra(
                                VideoSyncIntentService.INTENT_DELETE,
                                VideoSyncIntentService.NEW_DELETE);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Cannot refresh page, bad page number: " + mPage);
                }
                getActivity().startService(intent);
            }
        });
    }

    private class CustomGridLayoutManager extends GridLayoutManager{
        private boolean isScrollEnabled = true;

        public CustomGridLayoutManager(Context context, int columns){
            super(context, columns);
        }

        public void setScrollEnabled(boolean flag){
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically(){
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
