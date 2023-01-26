package com.example.apdatenote.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.apdatenote.Listeners.NoteListeners;
import com.example.apdatenote.R;
import com.example.apdatenote.adapter.NoteAdapter;
import com.example.apdatenote.database.NoteDataBase;
import com.example.apdatenote.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListeners{

    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_UPDATE = 2;
    public static final int REQUEST_SHOW_NOTE = 3;
    ImageView imageViewAddNote;
    RecyclerView recyclerView;
    List<Note> notesList;
    NoteAdapter noteAdapter;
    private int noteClickedPosition = -1;
    private EditText editTextSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewAddNote = findViewById(R.id.btn_add_main_activity);
        recyclerView = findViewById(R.id.rv_main_activity);
        editTextSearch = findViewById(R.id.et_main_activity_search);

        imageViewAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Create.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        });
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        notesList = new ArrayList<>();
        noteAdapter = new NoteAdapter(notesList, this);
        recyclerView.setAdapter(noteAdapter);

        getNotes(REQUEST_SHOW_NOTE, false);//this to displaying all note in database

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noteAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (notesList.size() != 0) {
                    noteAdapter.searchNotes(editable.toString());
                }
            }
        });

    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDataBase.getNoteDataBase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_SHOW_NOTE) {
                    notesList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD) {
                    notesList.add(0, notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE) {
                    notesList.remove(noteClickedPosition);
                    if (isNoteDeleted) {
                        noteAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        notesList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        noteAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD, false);//this to add new note to database
        } else if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), Create.class);
        intent.putExtra("viewUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }
}