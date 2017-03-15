package templar.atakr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Devin on 3/15/2017.
 */

public class VideoBrowseFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    //Layout related variables
    private int mPage;
    private RecyclerView mRecyclerView;

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
        mRecyclerView = (RecyclerView) view;
        return mRecyclerView;
    }
}
