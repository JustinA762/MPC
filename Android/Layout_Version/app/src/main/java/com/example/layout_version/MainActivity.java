package com.example.layout_version;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.layout_version.Account.Account;
import com.example.layout_version.Account.Account_Page;
import com.example.layout_version.Account.Account_Page_Profile;
import com.example.layout_version.MainTab.Streaming.ChannelItem;
import com.example.layout_version.MainTab.Streaming.StreamingFragment;
import com.example.layout_version.MainTab.Streaming.StreamingListFragment;
import com.example.layout_version.MainTab.Streaming.StreamingListFragmentInterface;
import com.example.layout_version.MainTab.Streaming.StreamingViewModel;
import com.example.layout_version.MainTab.Library.LibraryFragment;
import com.example.layout_version.MainTab.Library.LibraryFragmentInterface;
import com.example.layout_version.MainTab.Library.VideoDetailFragment;
import com.example.layout_version.MainTab.Library.VideoViewModel;
import com.example.layout_version.Settings.BackEnd;
import com.example.layout_version.Settings.*;


public class MainActivity extends AppCompatActivity implements LibraryFragmentInterface, StreamingListFragmentInterface {

    private Fragment libraryFragment;
    private VideoViewModel videoViewModel;
    private StreamingViewModel streamingViewModel;
    private Account account;
    private Button libraryTabButton;
    private Button cameraTabButton;

    private boolean videoDetailViewFlag;
    public static final boolean unit_testing = true;
    public static final boolean using_database_settings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(unit_testing){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            MyAsyncTask test = new MyAsyncTask(() -> {
                //Database_Manager.test(this);
                Intent intent = new Intent (MainActivity.this, Settings.class);
                startActivity(intent);
            });
            try {
                BackEnd.init();
                System.out.println("running async");
                test.execute();
                test.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        videoDetailViewFlag = false;

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        LibraryFragmentInterface.setUpNetwork(this, this, videoViewModel, 4);
        streamingViewModel = new ViewModelProvider(this).get(StreamingViewModel.class);
        StreamingListFragmentInterface.setUpNetwork(this, this, streamingViewModel, 4);


        ImageView btn = findViewById(R.id.settings);
        ImageView accountImageView = findViewById(R.id.account);
        libraryTabButton = findViewById(R.id.library);
        cameraTabButton = findViewById(R.id.view);

        account = Account.getInstance();
        if(!account.isSignedIn())
        {
            Intent intent = new Intent (MainActivity.this, Account_Page.class);
            startActivity(intent);
        }



        MyAsyncTask database = new MyAsyncTask(() -> {
            System.out.println("calling backend");
            BackEnd.init();
        });
        try {
            System.out.println("running async");
            database.execute();
            database.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        });

        accountImageView.setOnClickListener(view -> {

            Intent intent;
            if(account.isSignedIn())
                intent = new Intent (MainActivity.this, Account_Page_Profile.class);
            else
                intent = new Intent (MainActivity.this, Account_Page.class);

            startActivity(intent);
        });

        if(savedInstanceState == null) {
            Log.d("", "New state");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragmentContainerView, StreamingListFragment.newInstance())
                    .commit();
            libraryFragment =LibraryFragment.newInstance(true);
        }
        else
            Log.d("", "Npot New state");



        libraryTabButton.setOnClickListener(view -> {
            LibraryFragment fragment = (LibraryFragment)getSupportFragmentManager().findFragmentByTag("LibraryFragment");
            if (fragment != null && fragment.isVisible()) {
                return;
            }
            VideoDetailFragment videoDetailFragment = (VideoDetailFragment)getSupportFragmentManager().findFragmentByTag("VideoDetailFragment");
            if (videoDetailFragment != null && videoDetailFragment.isVisible()) {
                getSupportFragmentManager().popBackStack();
                videoDetailViewFlag = false;
                return;
            }
            cameraTabButton.setBackgroundColor(Color.parseColor("#ffffff"));
            libraryTabButton.setBackgroundColor(Color.parseColor("#c4fffd"));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainerView, libraryFragment, "LibraryFragment")
                    .addToBackStack("LibraryFragment")
                    .commit();
            if(videoDetailViewFlag)
            {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragmentContainerView, new VideoDetailFragment(), "VideoDetailFragment")
                        .addToBackStack("VideoDetailFragment")
                        .commit();
            }
        }
        );

        cameraTabButton.setOnClickListener(view -> {
            LibraryFragment fragment = (LibraryFragment)getSupportFragmentManager().findFragmentByTag("LibraryFragment");
            StreamingFragment streamingFragment = (StreamingFragment) getSupportFragmentManager().findFragmentByTag("StreamingFragment");
            if (fragment != null && fragment.isVisible() || streamingFragment != null && streamingFragment.isVisible()) {
                getSupportFragmentManager().popBackStack();
            }
            VideoDetailFragment videoDetailFragment = (VideoDetailFragment)getSupportFragmentManager().findFragmentByTag("VideoDetailFragment");
            if (videoDetailFragment != null && videoDetailFragment.isVisible()) {
                getSupportFragmentManager().popBackStack("LibraryFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            libraryTabButton.setBackgroundColor(Color.parseColor("#ffffff"));
            cameraTabButton.setBackgroundColor(Color.parseColor("#c4fffd"));
        });
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        LibraryFragment fragment = (LibraryFragment)getSupportFragmentManager().findFragmentByTag("LibraryFragment");
        VideoDetailFragment videoDetailFragment = (VideoDetailFragment)getSupportFragmentManager().findFragmentByTag("VideoDetailFragment");
        if (fragment != null && fragment.isVisible() ||
                videoDetailFragment != null && videoDetailFragment.isVisible()) {
            libraryTabButton.setBackgroundColor(Color.parseColor("#c4fffd"));
            cameraTabButton.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else{
            libraryTabButton.setBackgroundColor(Color.parseColor("#ffffff"));
            cameraTabButton.setBackgroundColor(Color.parseColor("#c4fffd"));
        }


    }

//    @Override
//    public void changed(String token) {
//        Log.e("", "Token changed");
//        videoViewModel.setToken(token);
//        streamingViewModel.setToken(token);
//    }

    @Override
    public void videoSelected() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragmentContainerView, new VideoDetailFragment(), "VideoDetailFragment")
                .setReorderingAllowed(true)
                .addToBackStack("VideoDetailFragment")
                .commit();
        videoDetailViewFlag = true;
    }

    @Override
    public void channelSelected(ChannelItem channelItem) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragmentContainerView, new StreamingFragment(), "StreamingFragment")
                .setReorderingAllowed(true)
                .addToBackStack("StreamingFragment")
                .commit();
    }


    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private Runnable task = null;
        MyAsyncTask(Runnable task) {
            this.task = task;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("doing in background");
            task.run();
            return null;
        }
    }

}