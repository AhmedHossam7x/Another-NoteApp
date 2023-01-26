package com.example.apdatenote.Listeners;

import androidx.cardview.widget.CardView;

import com.example.apdatenote.entities.Note;

public interface NoteListeners {
    void onNoteClicked(Note note, int position);
}
