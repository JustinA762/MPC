package com.example.layout_version.CameraShare;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.layout_version.Account.Account;
import com.example.layout_version.MainTab.Streaming.StreamingListFragmentInterface;
import com.example.layout_version.MainTab.Streaming.StreamingViewModel;
import com.example.layout_version.R;

public class CameraConnectDialog extends DialogFragment implements CameraConnectFragment.CameraShareInterface {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.device_connect, container, false);
        Dialog dialog = getDialog();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // 画面サイズの0.8倍の大きさに指定
        lp.width = (int) (metrics.widthPixels * 0.9);
        dialog.getWindow().setAttributes(lp);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // 画面サイズの0.8倍の大きさに指定
        int dialogWidth = (int) (metrics.widthPixels * 0.9);
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void action() {
        StreamingViewModel streamingViewModel = new ViewModelProvider(requireActivity()).get(StreamingViewModel.class);
        StreamingListFragmentInterface.loadData(getContext(), streamingViewModel, Account.getInstance().getTokenData().getValue(), 4);
        dismiss();
    }
}