package com.springapp.VideoLoadUtil;

import com.amazonaws.services.elastictranscoder.model.Thumbnails;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.springapp.DAC.VideoDAO;
import com.springapp.accessConfig.AccessConfig;
import com.springapp.accessConfig.ClientAmazonS3Factory;
import org.apache.commons.io.FileUtils;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Kirill on 8/20/2014.
 */
public class ThumbnailUtil {
    private static ThumbnailUtil instance;
    public static final String VIDEO_FILE_NAME = "test.mp4";
    public static final String IMAGE_FILE_NAME = "test.jpg";
    public static final String TYPE_OF_IMAGE = "jpg";
    public static final String TYPE_OF_VIDEO = "mp4";

    private ThumbnailUtil(){
    }

    public static ThumbnailUtil getInstance(){
        if (instance == null)
            instance = new ThumbnailUtil();
        return instance;
    }

    public void getAndSaveThumbnail(MultipartFile file) throws IOException {
        Files.deleteIfExists(Paths.get(VIDEO_FILE_NAME));
        Files.createFile(Paths.get(VIDEO_FILE_NAME));
        File fileVideo = new File(VIDEO_FILE_NAME);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(VIDEO_FILE_NAME);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedImage frame = null;
        File fileFrame = new File(VIDEO_FILE_NAME);
        try {
            frame = FrameGrab.getFrame(fileFrame, 1);
        } catch (JCodecException e) {
            e.printStackTrace();
        }

        String imageName = VideoDAO.getInstance().getLastId() + "." + TYPE_OF_IMAGE;

        ImageIO.write(frame, TYPE_OF_IMAGE, new File(IMAGE_FILE_NAME));
        File fileImage = new File(IMAGE_FILE_NAME);
        ByteArrayInputStream input = new ByteArrayInputStream(FileUtils.readFileToByteArray(fileImage));
        ClientAmazonS3Factory.getInstance().saveObject(input, imageName);
        fileImage.delete();
        Files.deleteIfExists(Paths.get(VIDEO_FILE_NAME));
    }
}
