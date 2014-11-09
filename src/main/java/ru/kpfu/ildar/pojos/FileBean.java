package ru.kpfu.ildar.pojos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileBean
{
    public enum Type { File, Folder };

    private String name;
    private double size;
    private Type type;
    private Date creationDate;
    private Date lastChangedDate;

    public FileBean() { }
    public FileBean(String name, double size, Type type, Date creationDate, Date lastChangedDate)
    {
        this.name = name;
        this.size = size;
        this.type = type;
        this.creationDate = creationDate;
        this.lastChangedDate = lastChangedDate;
    }
    public FileBean(OneDriveFile file)
    {
        this.name = file.getName();
        this.size = file.getSize();
        this.type = Type.valueOf(file.getType());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+0000'");
        try
        {
            this.creationDate = fmt.parse(file.getCreated_time());
            this.lastChangedDate = fmt.parse(file.getClient_updated_time());
        }
        catch(ParseException exc)
        {
            throw new RuntimeException(exc);
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getSize()
    {
        return size;
    }

    public void setSize(double size)
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
