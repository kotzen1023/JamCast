package com.seventhmoon.jamcast.data;

import android.widget.CheckBox;

public class FileChooseItem implements Comparable<FileChooseItem> {
    private String name;
    private String data;
    private String date;
    private String path;
    private String image;
    private CheckBox checkBox;

    public FileChooseItem(String n,String d, String dt, String p, String img)
    {
        super();
        this.name = n;
        this.data = d;
        this.date = dt;
        this.path = p;
        this.image = img;
    }
    public String getName()
    {
        return name;
    }
    public String getData()
    {
        return data;
    }
    public String getDate()
    {
        return date;
    }
    public String getPath()
    {
        return path;
    }
    public String getImage() {
        return image;
    }
    public CheckBox getCheckBox()
    {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public int compareTo(FileChooseItem o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
