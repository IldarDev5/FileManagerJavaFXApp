package ru.kpfu.ildar.filessource;

import ru.kpfu.ildar.pojos.FileBean;

import java.util.List;

public interface FilesSource
{
    List<FileBean> getElements(String path);
    void createNewElement(String path);
    void deleteElement(String path);
    void renameElement(String path);
}
