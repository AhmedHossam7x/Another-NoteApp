package com.example.apdatenote.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int ID= 0;
    @ColumnInfo(name = "title")
    private String title= "";
    @ColumnInfo(name = "data_time")
    private String dataTime= "";
    @ColumnInfo(name = "subtitle")
    private String subtitle= "";
    @ColumnInfo(name = "note_text")
    private String noteText= "";
    @ColumnInfo(name = "image_path")
    private String image= "";
    @ColumnInfo(name = "color")
    private String color= "";
    @ColumnInfo(name = "web_link")
    private String webLink= "";

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDataTime() {
        return dataTime;
    }
    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    public String getNoteText() {
        return noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getWebLink() {
        return webLink;
    }
    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    public String toString() {
        return title + " : " + dataTime;
    }
}
