package com.shashank.notes.viewModels;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.view.View;

import com.shashank.notes.model.Note;

import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by shashank on 10/06/16.
 */
public class NoteViewModel {

    public final Note note;

    public int positionInAdapter;

    public BehaviorSubject<Integer> onItemClicked = BehaviorSubject.create();

    public Boolean shouldFocusOnClick = true;

    public final BehaviorSubject<Integer> onDeleteClicked = BehaviorSubject.create();

    public final ObservableField<String> title;

    public final ObservableField<String> description;

    public NoteViewModel(final Note note) {
        this.note = note;
        title = new ObservableField<>(note.title);
        description = new ObservableField<>(note.description);

    }

    public void setPositionInAdapter(int positionInAdapter) {
        this.positionInAdapter = positionInAdapter;
    }

    public void onItemClick(View view) {
        onItemClicked.onNext(note.id);
    }

    public void updateNote() {
        note.title = title.get();
        note.description = description.get();
    }

    public Boolean isChangesMade() {
        return !title.get().equals(note.title) || !description.get().equals(note.description);
    }
}
