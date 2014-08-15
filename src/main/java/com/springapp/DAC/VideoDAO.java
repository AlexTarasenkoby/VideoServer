package com.springapp.DAC;

import com.springapp.DAC.config.util.ConnectionUtil;
import com.springapp.DAC.entities.Video;
import sun.rmi.runtime.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 8/11/2014.
 */
public class VideoDAO{

    private static VideoDAO instance;

    private VideoDAO(){

    }

    public static VideoDAO getInstance(){
        if (instance == null)
            instance = new VideoDAO();
        return instance;
    }

    public void add(Video video){
        String sql = "INSERT INTO video " +
                "VALUES (?,?,?,?,?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = ConnectionUtil.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, video.getCreatedDate());
            preparedStatement.setString(3, video.getDescription());
            preparedStatement.setString(4, video.getImageUrl());
            preparedStatement.setString(5, video.getVideoUrl());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Video> getAll(){
        String sql = "SELECT * FROM video ";
        ResultSet resultSet = null;
        Statement statement = null;
        List<Video> videos = new ArrayList<Video>();
        try {
            statement = ConnectionUtil.getConnection().createStatement();
            resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                Video video = new Video();
                video.setId(resultSet.getLong("ID"));
                video.setVideoUrl(resultSet.getString("VIDEO_URL"));
                video.setImageUrl(resultSet.getString("IMAGE_URL"));
                video.setDescription(resultSet.getString("DESCRIPTION"));
                video.setCreatedDate(resultSet.getString("CREATED_DATE"));
                videos.add(video);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }

    //select last_insert_id()
    public Long getLastId(){
        Statement statement = null;
        ResultSet rs = null;
        long id = 0;
        try {
            statement = ConnectionUtil.getConnection().createStatement();
            rs = statement.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public List<Video> getPage(int start, int limit){
        String sql = String.format("SELECT * FROM video LIMIT %d, %d ", start, limit);
        ResultSet resultSet = null;
        Statement statement = null;
        List<Video> videos = new ArrayList<Video>();
        try {
            statement = ConnectionUtil.getConnection().createStatement();
            resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                Video video = new Video();
                video.setId(resultSet.getLong("ID"));
                video.setVideoUrl(resultSet.getString("VIDEO_URL"));
                video.setImageUrl(resultSet.getString("IMAGE_URL"));
                video.setDescription(resultSet.getString("DESCRIPTION"));
                video.setCreatedDate(resultSet.getString("CREATED_DATE"));
                videos.add(video);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }
}
