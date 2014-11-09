package ru.kpfu.ildar.filessource;

import ru.kpfu.ildar.pojos.FileBean;

import java.util.List;

public class OneDriveFilesSource implements FilesSource
{

    @Override
    public List<FileBean> getElements(String path)
    {
        return null;
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
