package com.example.latihanfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
    public static final String ARTIST_ID = "artistid";



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
        lvartist.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener () {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get (i);

                showUpdateDialog (artist.getArtistId (),artist.getArtistName ());

                return false;
            }
        });
        lvartist.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get (i);

                Intent intent = new Intent (getApplicationContext (),AddTrack.class);

                intent.putExtra (ARTIST_ID,artist.getArtistId ());
                intent.putExtra (ARTIST_NAME,artist.getArtistName ());

                startActivity (intent);
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

    private void showUpdateDialog(final String artistID, String artistName){
        AlertDialog.Builder db = new AlertDialog.Builder (this);

        LayoutInflater inflater = getLayoutInflater ();

        final View dialogView = inflater.inflate (R.layout.update_dialog,null);

        db.setView (dialogView);

        final EditText etname = dialogView.findViewById (R.id.etUname);
        final Button btnupdate = dialogView.findViewById (R.id.btnUpdate);
        final Spinner sUp = dialogView.findViewById (R.id.SpinnerUpdate);
        final Button btnDelete = dialogView.findViewById (R.id.btnDelete);


        db.setTitle ("Update Artist "+artistName);

        final AlertDialog ad = db.create ();
        ad.show ();

        btnupdate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String Name = etname.getText ().toString ().trim ();
                String genre = sUp.getSelectedItem ().toString ();

                if (TextUtils.isEmpty (Name)){
                    etname.setError ("Name Required");
                    return;
                }else {
                    updateArtist (artistID,Name,genre);
                    ad.dismiss ();
                }

            }
        });

        btnDelete.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                deleteArtist (artistID);
                ad.dismiss ();
            }
        });

    }

    private void deleteArtist(String id){
        DatabaseReference dbartist = FirebaseDatabase.getInstance ().getReference ("artists").child (id);
        DatabaseReference dbtrack = FirebaseDatabase.getInstance ().getReference ("tracks").child (id);

        dbartist.removeValue ();
        dbtrack.removeValue ();

        Toast.makeText (this,"Delete successfully",Toast.LENGTH_LONG).show ();
    }

    private boolean updateArtist (String id , String name , String genre){
        DatabaseReference db = FirebaseDatabase.getInstance ().getReference ("artists").child (id);

        Artist artist = new Artist (id,name,genre);

        db.setValue (artist);
        Toast.makeText (this,"Update successfully",Toast.LENGTH_LONG).show ();
        return true;
    }


}
