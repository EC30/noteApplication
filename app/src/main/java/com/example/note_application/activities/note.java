package com.example.note_application.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.note_application.R;
import com.example.note_application.database.NoteDatabase;
import com.example.note_application.entities.Notes;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class note extends AppCompatActivity {
    private ImageView back,oval,delete, add_reminder;
    private TextView date_time;
    private Dialog dialog;

    private AlertDialog dialogDeleteNote;
    private EditText title,subTitle, content;
    private Notes alreadyAvailableNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        back=(ImageView)findViewById(R.id.back);
        oval=(ImageView)findViewById(R.id.oval);
        date_time=(TextView)findViewById(R.id.date_time);
        title=(EditText) findViewById(R.id.title);
        subTitle=(EditText) findViewById(R.id.subTitle);
        content=(EditText) findViewById(R.id.Content);
        delete=(ImageView)findViewById(R.id.delete);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        date_time.setText(
                new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );
        oval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
        //initMiscelleneous();
        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNotes =(Notes) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();

        }
        if(alreadyAvailableNotes != null){
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
                }
            });
        }


    }
    private void showDeleteDialog(){
        if(dialogDeleteNote == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(note.this);
            View view= LayoutInflater.from(this).inflate(
                    R.layout.delete_note,
                    (ViewGroup) findViewById(R.id.deleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote=builder.create();
            if(dialogDeleteNote.getWindow() !=null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.deleteText).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    class DeleteNoteTask extends  AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNotes);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent=new Intent();
                            intent.putExtra("iSNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }

                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
    }
    private void setViewOrUpdateNote(){
        title.setText((alreadyAvailableNotes.getTitle()));
        subTitle.setText((alreadyAvailableNotes.getSubTitle()));
        content.setText((alreadyAvailableNotes.getNote_text()));
        date_time.setText((alreadyAvailableNotes.getDate_time()));



    }
    private void saveNote(){
        if(title.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note must contain its title", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (subTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note must contain its subtitle", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(content.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please write some contain", Toast.LENGTH_SHORT).show();
            return;
        }

        final Notes notes=new Notes();
        notes.setTitle(title.getText().toString());
        notes.setSubTitle(subTitle.getText().toString());
        notes.setNote_text(content.getText().toString());
        notes.setDate_time(date_time.getText().toString());

        if(alreadyAvailableNotes !=null){
            notes.setId(alreadyAvailableNotes.getId());
        }


        @SuppressLint("StaticFieldLeak")
        class saveNote extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(notes);
            return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }

        new saveNote().execute();

    }



}