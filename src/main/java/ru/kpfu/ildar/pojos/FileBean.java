package ru.kpfu.ildar.pojos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileBean
{
    public enum Type { File, Folder };

    private String name;
    private long size;
    private Type type;
    private Date creationDate;
    private Date lastChangedDate;

    public FileBean() { }
    public FileBean(String name, long size, Type type, Date creationDate, Date lastChangedDate)
    {
        this.name = name;
        this.size = size;
        this.type = type;
        this.creationDate = creationDate;
        this.lastChangedDate = lastChangedDate;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getLastChangedDate()
    {
        return lastChangedDate;
    }

    public void setLastChangedDate(Date lastChangedDate)
    {
        this.lastChangedDate = lastChangedDate;
    }
}
