package com.example.layout_version.MainTab.Library;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_version.Account.Account;
import com.example.layout_version.MainTab.State.NetworkState;
import com.example.layout_version.MainTab.State.NetworkStateChangeListener;
import com.example.layout_version.MainTab.State.StateFragment;
import com.example.layout_version.R;

import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends StateFragment<NetworkState> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FIRST_TIME = "FIRST_TIME";

    private Context context;
    private RecyclerView videoRecyclerView;
    private VideoViewModel videoViewModel;
    private TextView libraryStatusTextView;
    private ImageButton refreshButton;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(Boolean firstTime) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putBoolean(FIRST_TIME, firstTime);
        fragment.setArguments(args);
        Log.e("", "New instance");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoViewModel = new ViewModelProvider(requireActivity()).get(VideoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View layout = inflater.inflate(R.layout.fragment_library, container, false);

        videoRecyclerView = layout.findViewById(R.id.videoRecyclerView);
        context = container.getContext();
        libraryStatusTextView = layout.findViewById(R.id.libraryStatusTextView);
        refreshButton = layout.findViewById(R.id.libraryRefreshButton);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Consumer<VideoItem> clickEvent = videoItem -> {
            videoViewModel.setSelectedItem(videoItem);
            if(requireActivity() instanceof LibraryFragmentInterface)
                ((LibraryFragmentInterface)requireActivity()).videoSelected();
            Log.e("Video Item", videoItem.getTitle());
        };

        VideoAdapter adapter = new VideoAdapter(videoViewModel.getDataList(), clickEvent);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        videoRecyclerView.setAdapter(adapter);
        videoRecyclerView.setLayoutManager(layoutManager);

        videoViewModel.getUpdateFlag().observe(getViewLifecycleOwner(), updateFlag -> {
            adapter.notifyDataSetChanged();
        });

        setStateChangeListener(new NetworkStateChangeListener(context, libraryStatusTextView, videoViewModel.getStateData().getValue()));
        videoViewModel.getStateData().observe(getViewLifecycleOwner(), this::setState);

        refreshButton.setOnClickListener(view1 -> {
            LibraryFragmentInterface.loadData(context, videoViewModel, Account.getInstance().getTokenData().getValue(), 4);
        });
    }
}