package com.springapp.mvc;

import com.amazonaws.services.elastictranscoder.model.Thumbnails;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.springapp.DAC.VideoDAO;
import com.springapp.DAC.entities.Video;
import com.springapp.JSonClasses.Item;
import com.springapp.JSonClasses.JsonResponse;
import com.springapp.JSonClasses.JsonResponseCollection;
import com.springapp.JSonClasses.JsonVideo;
import com.springapp.Uploader.ConfluentUploader;
import com.springapp.accessConfig.AccessConfig;
import com.springapp.accessConfig.ClientAmazonS3Factory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/video")
public class MainController {

	@RequestMapping(method = RequestMethod.GET)
    @ResponseBody
	public JsonResponseCollection printWelcome(ModelMap model,
                                    @RequestParam( value = "start", defaultValue = "0", required=false)  int start,
                                    @RequestParam(value = "limit", defaultValue = "50", required = false) int limit) {
        JsonResponseCollection jsonResponseCollection = new JsonResponseCollection();
        jsonResponseCollection.setSuccess(true);
        jsonResponseCollection.setMessage("OK");
        jsonResponseCollection.setTotal(VideoDAO.getInstance().getAll().size());
        List<Video> videos = VideoDAO.getInstance().getPage(start, limit);
        List<Item> items = new ArrayList<Item>();
        for (Video video:videos){
            Item item = new Item();
            item.setId(video.getId());
            item.setDescription(video.getDescription());
            item.setCreatedAt(video.getCreatedDate());
            items.add(item);
        }
        jsonResponseCollection.setData(items);
        return jsonResponseCollection;
	}

//    @RequestMapping(headers = "content-type=multipart/*", method = RequestMethod.POST)
//    public String upload(Model model,
//                         @RequestParam(value = "file") MultipartFile file) throws IOException {
//
//        ConfluentUploader confluentUploader = ConfluentUploader.getInstance();
//        confluentUploader.saveObject(file);
//        return "hello";
//    }

    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public JsonResponse handleFileUpload(MultipartHttpServletRequest httpServletRequest,
                                                @RequestParam("description") String description,
                                                @RequestParam("createdAt") String createdAt,
                                                @RequestParam("video") MultipartFile file) throws IOException, JCodecException {
        ConfluentUploader.getInstance().saveObject(file, createdAt, description);


        Files.deleteIfExists(Paths.get("test.mp4"));
        Files.createFile(Paths.get("test.mp4"));
        File fileVideo = new File("test.mp4");
        FileOutputStream fileOutputStream = new FileOutputStream("test.mp4");
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        BufferedImage frame = null;
//        FileOutputStream fos = new FileOutputStream(file.getOriginalFilename());
//        fos.write(file.getBytes());
//        fos.close();
        File fileFrame = new File("test.mp4");
        frame = FrameGrab.getFrame(fileFrame, 1);
//        File deletedFile = new File(file.getOriginalFilename());
//        deletedFile.delete();

        String imageName = VideoDAO.getInstance().getLastId() + ".jpg";

        ImageIO.write(frame, "jpg", new File("test.jpg"));
        File fileImage = new File("test.jpg");
        ByteArrayInputStream input = new ByteArrayInputStream(FileUtils.readFileToByteArray(fileImage));
        ClientAmazonS3Factory.getInstance()
                .getClient()
                .putObject(new PutObjectRequest(AccessConfig.NAMEOFBUCKET, imageName, input, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));
        fileImage.delete();
        return new JsonResponse(true, "OK");
   }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = {"Content-type=application/json"})
    @ResponseBody
    public JsonResponse update(@PathVariable("id") String id, JsonVideo video){
        String desc = video.createdAt;
//        Video entity = new Video();
//        entity.setCreatedDate(createdAt);
//        entity.setVideoUrl("");
//        entity.setImageUrl("");
//        entity.setDescription(description);
//        System.out.println(createdAt + description);
//        VideoDAO.getInstance().add(entity);
        return new JsonResponse(true, "OK");
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse delete(@PathVariable("id") String id)
    {
        String desc = id;
//        Video entity = new Video();
//        entity.setCreatedDate(createdAt);
//        entity.setVideoUrl("");
//        entity.setImageUrl("");
//        entity.setDescription(description);
//        System.out.println(createdAt + description);
//        VideoDAO.getInstance().add(entity);
        return new JsonResponse(true, "OK");
    }
}