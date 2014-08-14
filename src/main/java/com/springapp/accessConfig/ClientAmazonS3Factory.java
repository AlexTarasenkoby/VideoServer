package com.springapp.accessConfig;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by Kirill on 8/13/2014.
 */
public class ClientAmazonS3Factory {
    private static AWSCredentials credentials;
    private static AmazonS3 client;
    private static ClientAmazonS3Factory instance;

    private ClientAmazonS3Factory(){

    }

    public static ClientAmazonS3Factory getInstance(){
        if (instance == null)
            instance = new ClientAmazonS3Factory();
        return instance;
    }

    public AmazonS3 getClient(){
        if (client == null) {
            credentials = new BasicAWSCredentials(AccessConfig.ACCESSKEY, AccessConfig.SECRETKEY);
            client = new AmazonS3Client(credentials);
        }
        return client;
    }
}
