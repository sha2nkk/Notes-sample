package com.shashank.notes.model;

import android.databinding.Bindable;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by shashank on 10/06/16.
 */
public class Note extends RealmObject implements Serializable {

    @PrimaryKey
    public int id;
    public String title;
    public String description;

    public Note() {
        id = 0;
        this.title = "";
        this.description = "";
    }

    public Note(String title, String description) {
        id = 0;
        this.title = title;
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void generateId() {
        if (id != 0) {
            return;
        }
        try {
            Realm realm = Realm.getDefaultInstance();
            id = realm.where(this.getClass()).max("id").intValue() + 1;
        } catch (Exception ex) {
            id = 1;
        }
    }

    public static Note getNote(Realm realm, int noteId) {
        return realm.where(Note.class).equalTo("id", noteId).findFirst();
    }

}
