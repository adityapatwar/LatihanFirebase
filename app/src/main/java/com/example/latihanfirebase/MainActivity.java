package com.example.latihanfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME = "artistname";


    EditText etName;
    Button btnAdd;
    Spinner sItem;
    ListView lvartist;
    DatabaseReference dbArtist;
    List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        etName = findViewById (R.id.editText);
        btnAdd = findViewById (R.id.btnAdd);
        sItem = findViewById (R.id.Spinner);
        lvartist = findViewById (R.id.lvArtist);

        artistList = new ArrayList<> ();

        dbArtist = FirebaseDatabase.getInstance ().getReference ("artists");

        btnAdd.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                addArtist ();
            }
        });
        lvartist.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get (i);

                Intent intent = new Intent (getApplicationContext (),AddTrack.class);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart ();

        dbArtist.addValueEventListener (new ValueEventListener () {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artistList.clear ();

                for (DataSnapshot dsArtist:dataSnapshot.getChildren ()){
                    Artist artist = dsArtist.getValue (Artist.class);

                    artistList.add (artist);
                }
                ArtistList adapter = new ArtistList (MainActivity.this,artistList);
                lvartist.setAdapter (adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addArtist(){
        String Name = etName.getText ().toString ().trim ();
        String genre = sItem.getSelectedItem ().toString ();

        if(TextUtils.isEmpty (Name)){
            Toast.makeText (this,"Please Enter Your Name !",Toast.LENGTH_LONG).show ();
        }else {
            String id = dbArtist.push().getKey ();
            Artist ar = new Artist (id,Name,genre);
            dbArtist.child(id).setValue(ar);
            Toast.makeText (this,"Added",Toast.LENGTH_LONG).show ();
        }
    }


}
