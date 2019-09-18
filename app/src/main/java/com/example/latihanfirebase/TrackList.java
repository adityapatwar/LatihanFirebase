package com.example.latihanfirebase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TrackList extends ArrayAdapter<Track> {
    private Activity context;
    private List<Track> tracks;


    public TrackList(Activity context ,List<Track> tracks){
        super(context,R.layout.track_layout,tracks);
        this.context = context;
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater ();
        View view = inflater.inflate (R.layout.track_layout,null,true);

        TextView tvname = view.findViewById (R.id.tvTname);
        TextView tvrating = view.findViewById (R.id.tvRating);

        Track track = tracks.get(position);

        tvname.setText (track.getTrackName ());
        tvrating.setText (String.valueOf (track.getTrackRating ()) );

        return view;

    }
}
