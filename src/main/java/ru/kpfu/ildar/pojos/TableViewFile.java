package ru.kpfu.ildar.pojos;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String getPath()
    {
        return path;
    }
    public void setPath(String path)
    {
        this.path = path;
    }

    public String getFileName()
    {
        return fileName.get();
    }

    public void setFileName(String fileName)
    {
        this.fileName.set(fileName);
    }

    public long getSize()
    {
        return size.get();
    }

    public void setSize(long size)
    {
        this.size.set(size);
    }

    public String getChangedDate()
    {
        return changedDate.get();
    }

    public void setChangedDate(String changedDate)
    {
        this.changedDate.set(changedDate);
    }

    public String getType()
    {
        return type.get();
    }

    public void setType(String type)
    {
        this.type.set(type);
    }
}
