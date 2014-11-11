package ru.kpfu.ildar;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/** Utility class that helps in some cases */
public class FXUtils
{
    private static Image folderImage = null;

    /**
     * Get an icon image of the file with the specified extension, or of folder
     * @param extension File's extension
     * @param path Path to the element(full, including element's name)
     * @return Instance of javafx.scene.image.Image that represents this folder
     */
    public static Image getFXImage(String extension, String path)
    {
        File file = new File(path);
        if(!file.isFile() && folderImage != null)
            return folderImage;

        ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
        java.awt.Image awtImage = icon.getImage();
        BufferedImage bImg;
        if (awtImage instanceof BufferedImage)
            bImg = (BufferedImage) awtImage;
        else
        {
            bImg = new BufferedImage(awtImage.getWidth(null),
                    awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = bImg.createGraphics();
            graphics.drawImage(awtImage, 0, 0, null);
            graphics.dispose();
        }
        Image fxImage = SwingFXUtils.toFXImage(bImg, null);
        if(!file.isFile())
            folderImage = fxImage;

        return fxImage;
    }
}
