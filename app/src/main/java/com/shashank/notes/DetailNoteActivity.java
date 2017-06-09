package com.shashank.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.shashank.notes.databinding.ActivityNoteDetailBinding;
import com.shashank.notes.model.Note;
import com.shashank.notes.viewModels.NoteViewModel;

import io.realm.Realm;

/**
 * Created by shashank on 10/06/16.
 */
public class DetailNoteActivity extends AppCompatActivity {

    private ActivityNoteDetailBinding binding;
    private NoteViewModel viewModel;
    private Realm realm;
    private int noteId;
    private AlertDialog discardDialog;
    private AlertDialog saveChangesDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        initUI();
        setUpToolbar();

    }

    private void initUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_detail);
        bindData();
    }

    private void bindData() {
        noteId = getNoteId();
        if(noteId != 0 ) {
            viewModel = new NoteViewModel(Note.getNote(realm,noteId));
        } else {
            viewModel = new NoteViewModel(new Note());
        }
        binding.setVm(viewModel);
    }

    private int getNoteId() {
        Intent intent = getIntent();
        if(intent !=null && intent.getExtras() != null) {
            return intent.getExtras().getInt("id",0);
        }
        return 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveNote();
                break;
            case R.id.delete:
                showDiscardDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNote() {
        if(viewModel.note.id != 0) {
            realm.beginTransaction();
            viewModel.note.deleteFromRealm();
            realm.commitTransaction();
        }
        this.finish();
    }

    private void saveNote() {
        realm.beginTransaction();
        if(viewModel.note.id == 0 ) {
            viewModel.note.generateId();
        }
        viewModel.updateNote();
        realm.copyToRealm(viewModel.note);
        realm.commitTransaction();
        this.finish();
    }

    public void showDiscardDialog() {
        if(discardDialog == null) {
            discardDialog = new AlertDialog.Builder(this)
                    .setTitle(!TextUtils.isEmpty(viewModel.title.get()) ? viewModel.title.get() : "Note")
                    .setMessage("Do you want to discard this Note")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteNote();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }

        discardDialog.show();
    }

    public void showSaveDialog() {
        if(saveChangesDialog == null) {
            saveChangesDialog = new AlertDialog.Builder(this)
                    .setTitle(!TextUtils.isEmpty(viewModel.title.get()) ? viewModel.title.get() : "Note")
                    .setMessage("Do you want to save changes to  this Note")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote();
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
        }

        saveChangesDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(viewModel.isChangesMade()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }
}
