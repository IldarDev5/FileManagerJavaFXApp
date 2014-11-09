package ru.kpfu.ildar.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OneDriveFile
{
    private String id;
    private String name;
    private int size;
    private String type;
    private String created_time;
    private String client_updated_time;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCreated_time()
    {
        return created_time;
    }

    public void setCreated_time(String created_time)
    {
        this.created_time = created_time;
    }

    public String getClient_updated_time()
    {
        return client_updated_time;
    }

    public void setClient_updated_time(String client_updated_time)
    {
        this.client_updated_time = client_updated_time;
    }
}
