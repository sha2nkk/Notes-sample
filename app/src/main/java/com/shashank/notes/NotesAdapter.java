package com.shashank.notes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shashank.notes.databinding.ItemNoteBinding;
import com.shashank.notes.model.Note;
import com.shashank.notes.BR;
import com.shashank.notes.viewModels.NoteViewModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by shashank on 10/06/16.
 */
public class NotesAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private  List<NoteViewModel> noteList;
    private final Observable<List<NoteViewModel>> ObservalblenoteList;
    LayoutInflater inflater;
    public PublishSubject<Integer> onItemClicked;
    List<Subscription> lstSubscriptions;
    Realm realm;

    public NotesAdapter(final Context context, Observable<List<Note>> data) {
        inflater = LayoutInflater.from(context);
        noteList = new ArrayList<>();

        ObservalblenoteList = data.map(new Func1<List<Note>, List<NoteViewModel>>() {
            @Override
            public List<NoteViewModel> call(List<Note> notes) {
                List<NoteViewModel> noteVMList = new ArrayList<>();
                for (Note note : notes) {
                    noteVMList.add(new NoteViewModel(note));
                }
                return noteVMList;
            }
        });
        lstSubscriptions = new ArrayList<>();

        lstSubscriptions.add(ObservalblenoteList.subscribe(new Action1<List<NoteViewModel>>() {
            @Override
            public void call(List<NoteViewModel> noteViewModels) {
                noteList = noteViewModels;
                notifyDataSetChanged();
            }
        }));

        onItemClicked = PublishSubject.create();

        realm = Realm.getDefaultInstance();

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(ItemNoteBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        noteList.get(position).setPositionInAdapter(position);
        holder.binding.setVariable(BR.vm, noteList.get(position));
        ((ItemNoteBinding)holder.binding).delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                noteList.get(position).note.deleteFromRealm();
                realm.commitTransaction();
                noteList.remove(position);
                notifyItemRemoved(position);
                //notify all items below this to re-calculate positionInAdapter
                notifyItemRangeChanged(position, getItemCount() - position);
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onNext(noteList.get(position).note.id);
            }
        });
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }

    public void close() {
        for (Subscription subscription : lstSubscriptions) {
            subscription.unsubscribe();
        }
    }

}
