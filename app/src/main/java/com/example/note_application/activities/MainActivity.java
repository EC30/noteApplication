package com.example.note_application.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.note_application.R;
import com.example.note_application.adapter.NotesAdapter;
import com.example.note_application.database.NoteDatabase;
import com.example.note_application.entities.Notes;
import com.example.note_application.listener.NoteListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {
    public static final int REQUEST_CODE=1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTES=3;
    private ImageView add_notes;
    private EditText searchText;
    private RecyclerView notesRecyclerView;
    private List<Notes> notesList;
    public long longBackPressed;
    private NotesAdapter notesAdapter;
    private int noteClickedPosition=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchText=(EditText) findViewById(R.id.searchText);
        add_notes=(ImageView)findViewById(R.id.add_notes);
        add_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult
                        ( new Intent(getApplicationContext(), note.class),
                                REQUEST_CODE);


            }
        });
        notesRecyclerView=(RecyclerView)findViewById(R.id.notes_recycler_view);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );
        notesList=new ArrayList<>();
        notesAdapter=new NotesAdapter(notesList,this);
        notesRecyclerView.setAdapter(notesAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTES, false);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    notesAdapter.canceltimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(notesList.size() !=0){
                    notesAdapter.searchNotes(s.toString());
                }

            }
        });

    }

    @Override
    public void onNoteClicked(Notes notes, int position) {
        noteClickedPosition=position;
        Intent intent=new Intent(getApplicationContext(),note.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note",notes);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);


    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted){
        class getNoteTask extends AsyncTask<Void,Void, List<Notes>> {
            @Override
            protected List<Notes> doInBackground(Void... voids) {
                return NoteDatabase.getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Notes> notes) {
                super.onPostExecute(notes);
                if(requestCode == REQUEST_CODE_SHOW_NOTES){
                    notesList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }
                else if(requestCode ==REQUEST_CODE){
                    notesList.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode==REQUEST_CODE_UPDATE_NOTE){
                    notesList.remove(noteClickedPosition );
                    if(isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }else{
                        notesList.add(noteClickedPosition,notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

            }
        }
        new getNoteTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==REQUEST_CODE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE,false);
        }else if(requestCode ==REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if(data !=null){
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("iSNoteDeleted",false));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(longBackPressed + 2000 > System.currentTimeMillis()){
            new AlertDialog.Builder(this).setTitle("Do you want to exit ?").setNegativeButton("No",null).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(RESULT_OK,new Intent().putExtra("EXit",true));
                    finish();
                }
            }).create().show();

        }
        else{
            Toast.makeText(this, "Press Again to Exit", Toast.LENGTH_SHORT).show();
        }
        longBackPressed=System.currentTimeMillis();
    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        finish();
//    }
}