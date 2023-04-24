package com.example.layout_version.MainTab.Streaming;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;

import com.amazonaws.ivs.player.Player;
import com.amazonaws.ivs.player.PlayerView;
import com.example.layout_version.MainTab.State.StateFragment;
import com.example.layout_version.R;

public class StreamingFragment extends StateFragment<RecordingState> {
    private Context context;
    private StreamingViewModel streamingViewModel;
    private PlayerView streamingPlayerView;
    private Player streamingPlayer;
    private StreamingPlayerListener playerListener;
    private TextView deviceNameView;
    private TextView deviceStatusView;
    private ImageButton recordingButton;

    public StreamingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_streaming, container, false);
        context = layout.getContext();

        streamingPlayerView = new PlayerView(layout.getContext());
        FrameLayout playerFrameLayout = layout.findViewById(R.id.streamPlayerFrameView);
        streamingPlayerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        playerFrameLayout.addView(streamingPlayerView);
        deviceNameView = layout.findViewById(R.id.deviceNameView);
        deviceStatusView = layout.findViewById(R.id.deviceStatusView);
        recordingButton = layout.findViewById(R.id.recordingButton);

        streamingPlayer = streamingPlayerView.getPlayer();
        streamingPlayerView.getControls().showControls(false);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        streamingViewModel = new ViewModelProvider(requireActivity()).get(StreamingViewModel.class);
        streamingViewModel.getSelectedItem().observe(getViewLifecycleOwner(), item -> {
            Log.e("Observer", item.getDeviceName());
            update(item);
        });

        setStateChangeListener(new RecordingStateChangeListener(context, recordingButton, streamingViewModel.getRecordingStateData().getValue()));
        streamingViewModel.getRecordingStateData().observe(getViewLifecycleOwner(), recordingState -> {
            setState(recordingState);
        });
        recordingButton.setOnClickListener(view1 -> {
            RecordingState status = streamingViewModel.getRecordingStateData().getValue();
            if(status != RecordingState.BUFFERING)
            {
                if (status == RecordingState.STARTED)
                    streamingViewModel.setRecordingStatus(RecordingState.STOPPED);
                else
                    streamingViewModel.setRecordingStatus(RecordingState.STARTED);
            }
        });
    }

    public void update(ChannelItem channel)
    {
        deviceNameView.setText(channel.getDeviceName());
        if(channel.getPlaybackUrl() != null)
        {
            streamingPlayer.load(Uri.parse(channel.getPlaybackUrl()));
            playerListener = new StreamingPlayerListener(context, streamingPlayer, deviceStatusView, channel.getPlaybackUrl());
            streamingPlayer.addListener(playerListener);
        }else{
//            StreamingFragmentInterface.loadData(context, streamingViewModel, channel, 4);
            deviceStatusView.setBackground(AppCompatResources.getDrawable(context, R.drawable.unavailable_icon));
            deviceStatusView.setText(R.string.streaming_unavailable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        streamingPlayer.removeListener(playerListener);
        streamingPlayer.release();
    }


}
