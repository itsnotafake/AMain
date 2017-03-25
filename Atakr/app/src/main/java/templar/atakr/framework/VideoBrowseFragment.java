package templar.atakr.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import templar.atakr.R;
import templar.atakr.contentprovider.VideoContract;
import templar.atakr.databaseobjects.Video;
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
    private RecyclerView mRecyclerView;
    private VideoAdapter mVideoAdapter;
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
        //initializeRecyclerView();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        final int columns = getResources().getInteger(R.integer.main_list_columns);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columns));
        mRecyclerView.setAdapter(mVideoAdapter);

        //Assign progress bar
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
        return view;
    }

    public void initializeBroadcastReceiver(){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "Broadcast received");
                mProgressBar.setVisibility(View.GONE);
                mVideoAdapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiver, new IntentFilter(VIDEO_DATA_BROADCAST + mPage));
    }
}
