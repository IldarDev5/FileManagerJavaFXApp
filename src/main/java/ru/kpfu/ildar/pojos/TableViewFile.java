package ru.kpfu.ildar.pojos;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TableViewFile
{
    private SimpleStringProperty fileName;
    private SimpleDoubleProperty size;
    private SimpleStringProperty changedDate;
    private SimpleStringProperty type;

    public TableViewFile() { }
    public TableViewFile(String fileName, double size, Date changedDate, String type)
    {
        this.fileName = new SimpleStringProperty(fileName);
        this.size = new SimpleDoubleProperty(size);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        this.changedDate = new SimpleStringProperty(fmt.format(changedDate));
        this.type = new SimpleStringProperty(type);
    }

    public String getFileName()
    {
        return fileName.get();
    }

    public void setFileName(String fileName)
    {
        this.fileName.set(fileName);
    }

    public double getSize()
    {
        return size.get();
    }

    public void setSize(double size)
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
