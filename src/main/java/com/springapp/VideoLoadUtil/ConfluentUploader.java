package com.springapp.VideoLoadUtil;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.*;
import com.springapp.DAC.VideoDAO;
import com.springapp.DAC.entities.Video;
import com.springapp.accessConfig.AccessConfig;
import com.springapp.accessConfig.ClientAmazonS3Factory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Kirill on 8/13/2014.
 */
public class ConfluentUploader {
    private static ConfluentUploader instance;

    private ConfluentUploader(){

    }

    public static ConfluentUploader getInstance(){
        if (instance == null)
            instance = new ConfluentUploader();
        return instance;
    }

    public void saveObject(MultipartFile file, String createdDate, String description){
        VideoDAO videoDAO = VideoDAO.getInstance();
        Video video = new Video();
        video.setCreatedDate(createdDate);
        video.setDescription(description);
        video.setImageUrl("");
        video.setVideoUrl("");
        videoDAO.add(video);
        long id = videoDAO.getLastId();
        VideoDAO.getInstance().updateImageUrlVideoUrl(id, getImageUrl(id), getVideoUrl(id));
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientAmazonS3Factory.getInstance().saveObject(input, Integer.toString((int) id) + ".mp4");
    }

    private String getImageUrl(long id){
        return new StringBuilder()
                .append("http://")
                .append(AccessConfig.NAMEOFBUCKET)
                .append(".s3.amazonaws.com/")
                .append(id)
                .append(".")
                .append(ThumbnailUtil.TYPE_OF_IMAGE)
                .toString();
    }

    private String getVideoUrl(long id){
        return new StringBuilder()
                .append("http://")
                .append(AccessConfig.NAMEOFBUCKET)
                .append(".s3.amazonaws.com/")
                .append(id)
                .append(".")
                .append(ThumbnailUtil.TYPE_OF_VIDEO)
                .toString();
    }
}
