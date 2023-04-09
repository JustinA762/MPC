package com.example.layout_version;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.net.URI;


/**
 * The class Library extends application compat activity. This is the page where the recorded videos go to.
 */
public class Library extends AppCompatActivity {

    @Override

/**
 *
 * On create displays the page for library
 *
 * @param savedInstanceState  the saved instance state.
 */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        ImageView btn;
        Button view;
        ImageView account;

        btn = (ImageView) findViewById(R.id.settings);
        account = (ImageView) findViewById(R.id.account);
        view = (Button) findViewById(R.id.view);

        account.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this,Account_Page.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this,Settings.class);
                startActivity(intent);
            }
        });

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this,MainActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        RelativeLayout container = findViewById(R.id.captured_videos);
        int marg = 0;

        // The reuseable layout for libraries
        ViewGroup myLayout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.library_videos, null);
        myLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this, Library_Videos_Page.class);
                startActivity(intent);
            }
        });
        TextView video1 = myLayout.findViewById(R.id.video_description);
        video1.setText("Video 1");
        TextView timestamp1 = myLayout.findViewById(R.id.timeStamp);
        timestamp1.setText("2023-04-05 19:02:10");
        container.addView(myLayout);

        ViewGroup myLayout2 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.library_videos, null);
        params.setMargins(0, 230, 0, 10);
        myLayout2.setLayoutParams(params);
        myLayout2.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this, Library_Videos_Page.class);
                startActivity(intent);
            }
        });
        TextView video2 = myLayout2.findViewById(R.id.video_description);
        video2.setText("Video 2");
        TextView timestamp2 = myLayout2.findViewById(R.id.timeStamp);
        timestamp2.setText("2023-04-05 19:02:10");
        container.addView(myLayout2);

        ViewGroup myLayout3 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.library_videos, null);
        params.setMargins(0, 460, 0, 10);
        myLayout3.setLayoutParams(params);
        myLayout3.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this, Library_Videos_Page.class);
                startActivity(intent);
            }
        });
        TextView video3 = myLayout3.findViewById(R.id.video_description);
        video3.setText("Video 3");
        TextView timestamp3 = myLayout3.findViewById(R.id.timeStamp);
        timestamp3.setText("2023-04-05 19:02:10");
        container.addView(myLayout3);

        ViewGroup myLayout4 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.library_videos, null);
        params.setMargins(0, 690, 0, 10);
        myLayout4.setLayoutParams(params);
        myLayout4.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this, Library_Videos_Page.class);
                startActivity(intent);
            }
        });
        TextView video4 = myLayout4.findViewById(R.id.video_description);
        video4.setText("Video 4");
        TextView timestamp4 = myLayout4.findViewById(R.id.timeStamp);
        timestamp4.setText("2023-04-05 19:02:11");
        container.addView(myLayout4);

        ViewGroup myLayout5 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.library_videos, null);
        params.setMargins(0, 920, 0, 10);
        myLayout5.setLayoutParams(params);
        myLayout5.setOnClickListener(new View.OnClickListener()
        {
            @Override

/**
 *
 * On click enter into video view
 *
 * @param view  the view.
 */
            public void onClick(View view) {

                Intent intent = new Intent (Library.this, Library_Videos_Page.class);
                startActivity(intent);
            }
        });
        TextView video5 = myLayout5.findViewById(R.id.video_description);
        video5.setText("Video 5");
        TextView timestamp5 = myLayout5.findViewById(R.id.timeStamp);
        timestamp5.setText("2023-04-05 19:02:11");
        container.addView(myLayout5);

    }

}
