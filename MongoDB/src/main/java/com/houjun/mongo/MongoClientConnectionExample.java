package com.houjun.mongo;

import com.mongodb.ServerAddress;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;

public class MongoClientConnectionExample {
    public static void main(String[] args) {
        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "mongodb://admin:Passw0rd@k8s-master76:31170,k8s-master76:31171,k8s-master76:31172/admin?authSource=admin&replicaSet=hj12091546";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("admin");
            try {
                Bson command = new BsonDocument("ping", new BsonInt64(1));
                Document commandResult = database.runCommand(command);
                System.out.println("Connected successfully to server.");
            } catch (MongoException me) {
                System.err.println("An error occurred while attempting to run a command: " + me);
            }
        }
    }
}
