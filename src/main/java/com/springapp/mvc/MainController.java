package com.springapp.mvc;

import com.amazonaws.services.elastictranscoder.model.Thumbnails;
import com.springapp.DAC.VideoDAO;
import com.springapp.DAC.entities.Video;
import com.springapp.JSonClasses.Item;
import com.springapp.JSonClasses.JsonResponse;
import com.springapp.JSonClasses.JsonResponseCollection;
import com.springapp.JSonClasses.JsonVideo;
import com.springapp.Uploader.ConfluentUploader;
import com.springapp.videoUtility.VideoUtility;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
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
                                                @RequestParam("video") MultipartFile file) throws IOException {
//        Thumbnails
        ConfluentUploader.getInstance().saveObject(file, createdAt, description);
        return new JsonResponse(true, createdAt);
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