package ru.kpfu.ildar.pojos;

import java.util.Date;

/** Represents a file/folder entity - here we'll be storing information about the element */
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

    /** Get name of the element */
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /** Get size of the element in bytes */
    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    /** Get type of the element - whether it's a file, or a folder */
    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    /** Get element creation date */
    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    /** Get element last modified date */
    public Date getLastChangedDate()
    {
        return lastChangedDate;
    }

    public void setLastChangedDate(Date lastChangedDate)
    {
        this.lastChangedDate = lastChangedDate;
    }
}
