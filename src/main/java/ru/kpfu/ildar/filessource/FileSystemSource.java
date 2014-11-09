package ru.kpfu.ildar.filessource;

import ru.kpfu.ildar.pojos.FileBean;

import java.util.ArrayList;
import java.util.List;

public class FileSystemSource implements FilesSource
{
    @Override
    public List<FileBean> getElements(String path)
    {
        List<FileBean> lst = new ArrayList<>();

        

        return lst;
    }

    @Override
    public void createNewElement(String path)
    {

    }

    @Override
    public void deleteElement(String path)
    {

    }

    @Override
    public void renameElement(String path)
    {

    }
}
