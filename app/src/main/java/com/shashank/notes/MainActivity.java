package com.shashank.notes;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.shashank.notes.databinding.ActivityMainBinding;
import com.shashank.notes.model.Note;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Observable<List<Note>> noteList = BehaviorSubject.create();
    NotesAdapter adapter;
    Realm realm;
    List<Subscription> lstSubscription = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setUpToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
        initUI();
    }

    private void initData() {
        noteList = realm.where(Note.class).findAllAsync().asObservable().filter(new Func1<RealmResults<Note>, Boolean>() {
            @Override
            public Boolean call(RealmResults<Note> notes) {
                return notes.isLoaded() && notes.isValid();
            }
        }).map(new Func1<RealmResults<Note>, List<Note>>() {
            @Override
            public List<Note> call(RealmResults<Note> result) {
                return new ArrayList<>(result);
            }
        });

        lstSubscription.add(noteList.subscribe(new Action1<List<Note>>() {
            @Override
            public void call(List<Note> notes) {
                boolean dataVisibility = notes!=null && notes.size()>0;
                    binding.rvNotes.setVisibility(dataVisibility?View.VISIBLE:View.GONE);
                    binding.noNotes.setVisibility(dataVisibility?View.GONE:View.VISIBLE);
            }
        }));

    }

    private void initUI() {
        binding.rvNotes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new NotesAdapter(this, noteList);
        binding.rvNotes.setAdapter(adapter);
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DetailNoteActivity.class);
                startActivity(i);
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lstSubscription.add(adapter.onItemClicked.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer id) {
                Intent detailNoteIntent = new Intent(MainActivity.this, DetailNoteActivity.class);
                detailNoteIntent.putExtra("id", id);
                startActivity(detailNoteIntent);
            }
        }));
    }

    @Override
    protected void onStop() {
        for (Subscription subscription : lstSubscription) {
            subscription.unsubscribe();
        }
        adapter.close();
        super.onStop();
    }
}
