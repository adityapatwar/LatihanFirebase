package com.example.latihanfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrack extends AppCompatActivity {

    TextView tvArtistName;
    EditText etTrackName;
    SeekBar sbTrack;

    ListView lvTrack;

    Button btnAddT;
    DatabaseReference dbTrack;

    List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_track);

        tvArtistName = findViewById (R.id.tvArtistName);
        etTrackName = findViewById (R.id.etTreackName);
        sbTrack = findViewById (R.id.sbRating);

        btnAddT = findViewById (R.id.btnAddTrack);
        lvTrack = findViewById (R.id.lvTrack);

        tracks = new ArrayList<> ();

        Intent intent = getIntent ();

        String id = intent.getStringExtra (MainActivity.ARTIST_ID);
        String name = intent.getStringExtra (MainActivity.ARTIST_NAME);

        tvArtistName.setText (name);

        dbTrack = FirebaseDatabase.getInstance ().getReference ("tracks").child (id);
        btnAddT.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                saveTrack();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart ();

        dbTrack.addValueEventListener (new ValueEventListener () {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tracks.clear ();

                for (DataSnapshot dsTrack:dataSnapshot.getChildren ()){
                    Track track = dsTrack.getValue (Track.class);

                    tracks.add (track);
                }
                TrackList adapter = new TrackList (AddTrack.this,tracks);
                lvTrack.setAdapter (adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveTrack() {
        String trackName = etTrackName.getText ().toString ().trim ();
        int rating = sbTrack.getProgress ();
        if(TextUtils.isEmpty (trackName)){
            Toast.makeText (this,"Track Name Should not Empty",Toast.LENGTH_LONG).show ();
        }else{
            String id = dbTrack.push ().getKey ();

            Track track = new Track (id,trackName,rating);
            dbTrack.child (id).setValue (track);

            Toast.makeText (this,"Track Saved Successfully",Toast.LENGTH_LONG).show ();
        }
    }
}
