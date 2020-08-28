package com.example.note_application.adapter;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note_application.R;
import com.example.note_application.entities.Notes;
import com.example.note_application.listener.NoteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private List<Notes> notes;
    private NoteListener noteListener;
    private Timer timer;
    private List<Notes> noteSource;

    public NotesAdapter(List<Notes> notes, NoteListener noteListener) {
        this.noteListener=noteListener;
        this.notes = notes;
        noteSource=notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         return new NoteViewHolder(
                 LayoutInflater.from(parent.getContext()).inflate(
                         R.layout.item_container,
                         parent,
                         false
                 )
         );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
            holder.setNote(notes.get(position));
            holder.layoutNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noteListener.onNoteClicked(notes.get(position),position);
                }
            });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
       TextView textTitle,textSubtitle,textDate_time;
       LinearLayout layoutNote;
         NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutNote=itemView.findViewById(R.id.linearLayoutNotes);
            textTitle= itemView.findViewById(R.id.textTitle);
             textSubtitle= itemView.findViewById(R.id.textSubtitle);
             textDate_time = itemView.findViewById(R.id.textDateTime);


        }
        void setNote(Notes notes){
             textTitle.setText(notes.getTitle());
             if(notes.getSubTitle().trim().isEmpty()){
                 textSubtitle.setVisibility(View.GONE);
             }else{
                 textSubtitle.setText(notes.getSubTitle());
             }
             textDate_time.setText(notes.getDate_time());

        }
    }
    public void searchNotes(final  String searchKeyword){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    notes=noteSource;
                }else{
                    ArrayList<Notes> temp=new ArrayList<>();
                    for(Notes notes :noteSource){
                       if (notes.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                           || notes.getSubTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || notes.getNote_text().toLowerCase().contains(searchKeyword.toLowerCase())){
                                    temp.add(notes);
                        }
                    }
                    notes=temp;
                }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                                notifyDataSetChanged();
                        }
                    });
            }
        },500);
    }
    public void canceltimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}
