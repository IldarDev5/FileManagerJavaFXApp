package ru.kpfu.ildar.filessource;

import javafx.scene.image.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import ru.kpfu.ildar.pojos.FileBean;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileSystemSource
{
    public List<FileBean> getElements(String path) throws IllegalAccessException, FileNotFoundException
    {
        List<FileBean> lst = new ArrayList<>();

        File fl = new File(path);
        if(!fl.exists())
            throw new FileNotFoundException();

        File[] filesArr = fl.listFiles();
        if(filesArr == null)
            throw new IllegalAccessException();

        List<File> files = Arrays.asList(filesArr);
        files.forEach((f) ->
        {
            try
            {
                Date lastModifDate = new Date(f.lastModified());
                long creationTime = getCreationTime(path);

                lst.add(new FileBean(f.getName(), f.length(),
                            f.isFile() ? FileBean.Type.File : FileBean.Type.Folder,
                            new Date(creationTime), lastModifDate));
            }
            catch(IOException exc)
            {
                exc.printStackTrace();
            }
        });


        return lst;
    }

    private long getCreationTime(String path) throws IOException
    {
        Path p = Paths.get(path);
        return Files.getFileAttributeView(p, BasicFileAttributeView.class)
                .readAttributes().creationTime().toMillis();
    }

    public boolean createNewElement(String path, boolean isFile) throws IOException
    {
        File fl = new File(path);
        return isFile ? fl.createNewFile() : fl.mkdirs();
    }

    public boolean deleteElement(String path)
    {
        File fl = new File(path);
        if(fl.isFile())
            return fl.delete();
        else
        {
            try
            {
                FileUtils.deleteDirectory(fl);
                return true;
            }
            catch(IOException exc) { return false; }
        }
    }

    public boolean renameElement(String path, String newName)
    {
        File fl = new File(path);
        return fl.renameTo(new File(fl.getAbsolutePath() + File.separator + newName));
    }

    public void copyElement(String srcPath, String destPath) throws IOException
    {
        File fl = new File(srcPath);
        String path = destPath + File.separator + fl.getName();
        if(fl.isFile())
        {
            try(FileInputStream is = new FileInputStream(srcPath);
                FileOutputStream os = new FileOutputStream(path))
            {
                byte[] bt = new byte[1024];
                int read;
                while((read = is.read(bt)) != -1)
                    os.write(bt, 0, read);
            }
        }
        else
        {
            FileUtils.copyDirectory(new File(srcPath), new File(path));
        }
    }

    public void openFile(String fileName, String currPath) throws IOException
    {
        Desktop.getDesktop().open(new File(currPath + File.separator + fileName));
    }

    public boolean isFile(String fileName, String currPath)
    {
        File fl = new File(currPath + File.separator + fileName);
        return fl.isFile();
    }

    public boolean checkExistence(String path)
    {
        try
        {
            File file = File.createTempFile("ololo", ".doc");
            FileSystemView view = FileSystemView.getFileSystemView();
            Icon icon = view.getSystemIcon(file);
        }
        catch(Exception exc) { }

        File fl = new File(path);
        return fl.exists();
    }

    public String getExtension(String name)
    {
        return FilenameUtils.getExtension(name);
    }
}
