package ru.kpfu.ildar.pojos;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

/** A POJO-class that is used for binding elements data to the TableView instance */
public class TableViewFile
{
    private SimpleStringProperty fileName;
    private SimpleLongProperty size;
    private SimpleStringProperty changedDate;
    private SimpleStringProperty type;
    private String path;

    public TableViewFile() { }
    public TableViewFile(String fileName, long size, Date changedDate, String type, String path)
    {
        this.fileName = new SimpleStringProperty(fileName);
        this.size = new SimpleLongProperty(size);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy kk:mm");
        this.changedDate = new SimpleStringProperty(fmt.format(changedDate));
        this.type = new SimpleStringProperty(type);
        this.path = path;
    }

    /** Get path to the file/folder */
    public String getPath()
    {
        return path;
    }
    public void setPath(String path)
    {
        this.path = path;
    }

    /** Get file/folder name */
    public String getFileName()
    {
        return fileName.get();
    }

    public void setFileName(String fileName)
    {
        this.fileName.set(fileName);
    }

    /** Get file/folder size in bytes */
    public long getSize()
    {
        return size.get();
    }

    public void setSize(long size)
    {
        this.size.set(size);
    }

    /** Get last modified date */
    public String getChangedDate()
    {
        return changedDate.get();
    }

    public void setChangedDate(String changedDate)
    {
        this.changedDate.set(changedDate);
    }

    /** Get type of this element - whether it's a file, or a folder */
    public String getType()
    {
        return type.get();
    }

    public void setType(String type)
    {
        this.type.set(type);
    }
}
