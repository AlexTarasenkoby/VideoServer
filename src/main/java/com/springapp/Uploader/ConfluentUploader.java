package com.springapp.Uploader;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.springapp.DAC.VideoDAO;
import com.springapp.DAC.entities.Video;
import com.springapp.accessConfig.AccessConfig;
import com.springapp.accessConfig.ClientAmazonS3Factory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

    public void saveObject(MultipartFile file){
        ClientAmazonS3Factory clientAmazonS3Factory = ClientAmazonS3Factory.getInstance();
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientAmazonS3Factory.getClient().putObject(AccessConfig.NAMEOFBUCKET, file.getOriginalFilename(), input, new ObjectMetadata());
        VideoDAO videoDAO = VideoDAO.getInstance();
        Video video = new Video();
        video.setCreatedDate("");
        video.setDescription("");
        video.setImageUrl("");
        video.setVideoUrl(file.getOriginalFilename());
        videoDAO.add(video);
    }
}
