package com.example.layout_version.SenderStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.ivs.broadcast.BroadcastException;
import com.amazonaws.ivs.broadcast.BroadcastSession;
import com.amazonaws.ivs.broadcast.Device;
import com.amazonaws.ivs.broadcast.ImageDevice;
import com.amazonaws.ivs.broadcast.ImagePreviewView;
import com.amazonaws.ivs.broadcast.Presets;
import com.amazonaws.ivs.player.PlayerView;
import com.example.layout_version.MainActivity;
import com.example.layout_version.R;

import android.Manifest;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LiveStreamActivity extends AppCompatActivity implements LivestreamBroadcastListener.LiveStreamInterface {

    public static final String INGEST_ENDPOINT = "ingest_endpoint";
    public static final String STREAM_KEY = "stream_key";
    private FrameLayout previewHolder;
    private BroadcastSession.Listener broadcastListener;
    private BroadcastSession broadcastSession;
    private TextView liveStatusTextView;
    private ImageButton streamRefreshButton;

    private String ingestEndpoint;
    private String streamKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);

        liveStatusTextView = findViewById(R.id.liveStatusTextView);
        streamRefreshButton = findViewById(R.id.streamRefreshButton);
        broadcastListener = new LivestreamBroadcastListener(this, liveStatusTextView, this);

        Intent intent = getIntent();
        ingestEndpoint = intent.getStringExtra(INGEST_ENDPOINT);
        streamKey = intent.getStringExtra(STREAM_KEY);



        final String[] requiredPermissions =
                { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };


        boolean flag = false;
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // If any permissions are missing we want to just request them all.
                ActivityCompat.requestPermissions(this, requiredPermissions, 0x100);
                flag = true;
                break;
            }
        }

        if(!flag)
        {
            setupBroadcastSession();
        }

        streamRefreshButton.setOnClickListener(v->{
            stop();
            setupBroadcastSession();
        });
    }

    public void setupBroadcastSession()
    {
        // Create a broadcast-session instance and sign up to receive broadcast
        // events and errors.
        Context ctx = getApplicationContext();
        broadcastSession = new BroadcastSession(ctx,
                broadcastListener,
                Presets.Configuration.STANDARD_PORTRAIT,
                Presets.Devices.FRONT_CAMERA(ctx));

        // awaitDeviceChanges will fire on the main thread after all pending devices
        // attachments have been completed

        previewHolder = findViewById(R.id.frameLayout);
        broadcastSession.awaitDeviceChanges(() -> {
            for(Device device: broadcastSession.listAttachedDevices()) {
                // Find the camera we attached earlier
                if(device.getDescriptor().type == Device.Descriptor.DeviceType.CAMERA) {
                    ImagePreviewView preview = ((ImageDevice)device).getPreviewView();
                    preview.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    previewHolder.addView(preview);
                }
            }
        });

        broadcastSession.start(ingestEndpoint, streamKey);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        if (requestCode == 0x100) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }
            setupBroadcastSession();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastSession != null)
        {
            broadcastSession.stop();
            broadcastSession.release();
            if(previewHolder != null)
                previewHolder.removeAllViews();

        }
    }

    @Override
    public void stop() {
        if(broadcastSession != null)
        {
            broadcastSession.stop();

            if(previewHolder != null)
                previewHolder.removeAllViews();
            broadcastSession.release();
        }
    }

    @Override
    public void reconnect() {
        if(broadcastSession != null)
        {
            stop();
            setupBroadcastSession();
        }
    }
}