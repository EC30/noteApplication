package com.example.note_application.listener;

import com.example.note_application.entities.Notes;

public interface NoteListener {
    void onNoteClicked(Notes notes, int position);
}
