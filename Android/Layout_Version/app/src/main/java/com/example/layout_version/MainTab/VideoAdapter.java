package com.example.layout_version.MainTab;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layout_version.R;

import java.util.List;
import java.util.function.Consumer;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final List<VideoItem> localDataSet;
    private final Consumer<VideoItem> clickEvent;
    private Context context;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView descriptionView;
        private final View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            // Define click listener for the ViewHolder's View
            titleView = view.findViewById(R.id.video_title);
            descriptionView = view.findViewById(R.id.video_description);
            view.setOnClickListener(v -> {

            });

        }
        public View getView()
        {
            return view;
        }
        public TextView getTitleView() {
            return titleView;
        }
        public TextView getDescriptionView() {
            return descriptionView;
        }

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public VideoAdapter(List<VideoItem> dataSet, Consumer<VideoItem> clickEvent) {
        localDataSet = dataSet;
        this.clickEvent = clickEvent;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.library_videos, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTitleView().setText(localDataSet.get(position).getTitle());
        viewHolder.getDescriptionView().setText(localDataSet.get(position).getDescription());
        viewHolder.getView().setOnClickListener(
                view -> clickEvent.accept(localDataSet.get(position))
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}