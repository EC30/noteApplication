package com.example.note_application.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.note_application.dao.Notedao;
import com.example.note_application.entities.Notes;

@Database(entities = Notes.class,version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase notedatabase;
    public static  synchronized NoteDatabase getDatabase(Context context){
        if(notedatabase == null){
            notedatabase= Room.databaseBuilder(
                    context,
                    NoteDatabase.class,
                    "notes_db"
            ).build();
        }
        return notedatabase;
    }
    public abstract Notedao noteDao();

}
