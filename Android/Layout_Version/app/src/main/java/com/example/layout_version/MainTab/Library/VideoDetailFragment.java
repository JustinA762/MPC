package com.example.layout_version.MainTab.Library;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.layout_version.R;

public class VideoDetailFragment extends Fragment {


    private Context context;
    private VideoViewModel videoViewModel;
    private TextView titleView;
    private VideoView videoView;
    private ImageButton playButton;
    private ImageButton stopButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_video_detail, container, false);
        context = layout.getContext();

        titleView = layout.findViewById(R.id.videoTitleView);
        videoView = layout.findViewById(R.id.videoView);
        playButton = layout.findViewById(R.id.playButton);
        stopButton = layout.findViewById(R.id.stopButton);

        videoViewModel = new ViewModelProvider(requireActivity()).get(VideoViewModel.class);
        videoViewModel.getSelectedItem().observe(getViewLifecycleOwner(), this::update);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView.setMediaController(new MediaController(context));
        playButton.setOnClickListener(v -> {
            videoView.seekTo(1);
            videoView.start();
        });

        stopButton.setOnClickListener(v -> {
            videoView.stopPlayback();
        });
    }

    public void update(@Nullable VideoItem video)
    {
        if(video == null)
        {
            titleView.setText("Unknown");
            videoView.setVideoPath(null);
            return;
        }
        titleView.setText(video.getTitle());
        if(video.getUrl() != null)
            videoView.setVideoPath(video.getUrl());
    }
}