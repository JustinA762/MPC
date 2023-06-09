package com.example.layout_version.Account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.toolbox.Volley;
import com.example.layout_version.Notifications;
import com.example.layout_version.R;

public class Account_Page_Forgot_Password extends AppCompatActivity {
    private Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_reset);
        account = Account.getInstance();

        Notifications notif = new Notifications(this);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        notif.send_Forgot_Password_Notification(managerCompat);

        TextView username = findViewById(R.id.username);

        Button sendBtn = findViewById(R.id.send);

        ImageView back_home_im = findViewById(R.id.back_home_btn_setting);
        TextView back_home_txt = findViewById(R.id.back_home_text_setting);

        sendBtn.setOnClickListener(v ->
                account.reset(
                        Account_Page_Forgot_Password.this,
                        username.getText().toString(),
                        () -> startActivity(new Intent (
                                Account_Page_Forgot_Password.this,
                                            Account_Page_Verify_Code.class
                                )
                        ),
                        () -> {}
                )
        );

        back_home_im.setOnClickListener(view -> onBackPressed());

        back_home_txt.setOnClickListener(view -> onBackPressed());

    }

}
