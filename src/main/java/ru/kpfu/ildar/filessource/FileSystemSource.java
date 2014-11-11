package ru.kpfu.ildar.filessource;

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

/** Source of files/folders information */
public class FileSystemSource
{
    /**
     * Returns list of files and folders located in the specified path
     * @param path Path where to take elements from
     * @throws IllegalAccessException Thrown if the code isn't able to take elements from this path
     * @throws FileNotFoundException Thrown if there's no folder with such path
     */
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

    /**
     * Get the time of file/folder creation
     * @throws IOException Thrown if an I/O error occured
     */
    private long getCreationTime(String path) throws IOException
    {
        Path p = Paths.get(path);
        return Files.getFileAttributeView(p, BasicFileAttributeView.class)
                .readAttributes().creationTime().toMillis();
    }

    /**
     * Create new element(file or folder) in the specified folder
     * @param path Folder where to create new element
     * @param isFile If true, then create file; otherwise create folder
     * @return true, if creation was successful
     * @throws IOException thrown if an I/O error occured
     */
    public boolean createNewElement(String path, boolean isFile) throws IOException
    {
        File fl = new File(path);
        return isFile ? fl.createNewFile() : fl.mkdirs();
    }

    /**
     * Delete the element with specified path - file or folder
     * @param path Path of the file/folder to delete
     * @return True, if deletion was successful
     */
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

    /**
     * Rename the specified element - file or folder
     * @param path Path to the element to rename
     * @param newName New name of the specified element
     * @return True, if the element was successfully renamed
     */
    public boolean renameElement(String path, String newName)
    {
        File fl = new File(path);
        return fl.renameTo(new File(fl.getAbsolutePath() + File.separator + newName));
    }

    /**
     * Copy an element from one path to another
     * @param srcPath Path of the element to copy
     * @param destPath Destination for the element copy
     * @throws IOException thrown if an I/O error occurred
     */
    public void copyElement(String srcPath, String destPath) throws IOException
    {
        File fl = new File(srcPath);
        String path = destPath + File.separator + fl.getName();
        if(fl.isFile())
            FileUtils.copyFile(new File(srcPath), new File(path));
        else
            FileUtils.copyDirectory(new File(srcPath), new File(path));
    }

    /**
     * Open a file/folder as an operating system process
     * @param fileName Name of the file/folder to open
     * @param currPath Path of the element to open
     * @throws IOException Thrown if an I/O error occurred
     */
    public void openFile(String fileName, String currPath) throws IOException
    {
        Desktop.getDesktop().open(new File(currPath + File.separator + fileName));
    }

    /**
     * Returns true, if this elements represents a file; otherwise false
     * @param fileName Name of the element to check
     * @param currPath Path Path of the element to check
     */
    public boolean isFile(String fileName, String currPath)
    {
        File fl = new File(currPath + File.separator + fileName);
        return fl.isFile();
    }

    /**
     * Check existence of the specified element
     * @param path Path of the element to check
     * @return True, if this file/folder exists
     */
    public boolean checkExistence(String path)
    {
        File fl = new File(path);
        return fl.exists();
    }

    /**
     * Returns an extension of a file/folder. If this is a folder, returns an empty string.
     * @param name Name of the file/folder
     */
    public String getExtension(String name)
    {
        return FilenameUtils.getExtension(name);
    }
}
