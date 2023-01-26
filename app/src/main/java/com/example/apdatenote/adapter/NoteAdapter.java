package com.example.apdatenote.adapter;

//import static android.graphics.Color.parseColor;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apdatenote.Listeners.NoteListeners;
import com.example.apdatenote.R;
import com.example.apdatenote.entities.Note;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

    private List<Note> noteList;
    private NoteListeners noteListeners;
    private Timer timer;
    private List<Note> notesSource;

    public NoteAdapter(List<Note> noteList, NoteListeners noteListeners) {
        this.noteList = noteList;
        this.noteListeners = noteListeners;
        notesSource= noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setNote(noteList.get(position));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteListeners.onNoteClicked(noteList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView textView_title, textView_subtitle, textView_time;
        LinearLayout linearLayout;
        RoundedImageView imageView;

         NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_title= itemView.findViewById(R.id.textTitle_item_design);
            textView_subtitle= itemView.findViewById(R.id.textSubtitle_item_design);
            textView_time= itemView.findViewById(R.id.timeDate_item_design);
            linearLayout= itemView.findViewById(R.id.layout_item_design);
            imageView= itemView.findViewById(R.id.imageNote_item_design);

        }

        void setNote(Note note){
             textView_title.setText(note.getTitle());
             if(note.getSubtitle().trim().isEmpty()){
                 textView_subtitle.setVisibility(View.GONE);
             }else {
                 textView_subtitle.setText(note.getSubtitle());
             }
//             textView_subtitle.setText(note.getSubtitle());
             textView_time.setText(note.getDataTime());

             GradientDrawable gradientDrawable= (GradientDrawable) linearLayout.getBackground();

             if(note.getColor() != null){
                 gradientDrawable.setColor(Color.parseColor(note.getColor()));
             } else {
                 gradientDrawable.setColor(Color.parseColor("#333333"));
             }

             if(note.getImage() != null){
                 imageView.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
                 imageView.setVisibility(View.VISIBLE);
             }else {
                 imageView.setVisibility(View.GONE);
             }
        }

    }
    public void searchNotes(final String searchKeyWord){
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyWord.trim().isEmpty()){
                    noteList= notesSource;
                }else {
                    ArrayList<Note> temp= new ArrayList<>();
                    for(Note note: notesSource){
                        if (note.getTitle().toLowerCase().contains(searchKeyWord.toLowerCase())
                            || note.getSubtitle().toLowerCase().contains(searchKeyWord.toLowerCase())
                            || note.getNoteText().toLowerCase().contains(searchKeyWord.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    noteList= temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }
    public void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }
}
