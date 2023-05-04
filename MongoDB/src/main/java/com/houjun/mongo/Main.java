package com.houjun.mongo;// JDK 8及以上。

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import javax.net.ssl.SSLContext;
import java.util.Arrays;

public class Main {

    public static String user = "admin";
    public static String databaseName = "admin";
    public static String password = "Passw0rd";
    public static String demoDb = "houjuntest";
    public static String demoColl = "xinshu";

    public static void main(String[] args) {
//        System.setProperty("javax.net.ssl.trustStore", "D:\\Program Files\\Java\\jdk1.8.0_281\\lib\\security\\cacerts");
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

        MongoClient mongoClient = Main.getClient2();
        MongoDatabase database = mongoClient.getDatabase(demoDb);
        MongoCollection<Document> collection = database.getCollection(demoColl);
        // 写入一条记录。
        try {
            InsertOneResult result = collection.insertOne(new Document()
                    .append("DEMO", "Java for 8888888888888888888")
                    .append("MESG", "Hello AliCoudDB For MongoDB"));
            System.out.println("Success! Inserted document id: " + result.getInsertedId());
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
        }

        // 查询第一条记录。
        Document doc = collection.find().first();
        System.out.println(doc.toJson());
    }

    public static MongoClient getClient() {
        // 建议使用副本集高可用地址，确保高可用。
        // 确保执行代码的服务器和MongoDB实例网络是连通的。
        // 如果密码中包含特殊字符，请进行转义处理。
//        String uri = "mongodb://admin:Passw0rd@k8s-master76:31170,k8s-master76:31171,k8s-master76:31172/admin?authSource=admin&tls=true&tlsInsecure=true";
//        String uri = "mongodb://admin:Passw0rd@192.168.12.76:31180,192.168.12.76:31181,192.168.12.76:31182/admin?authSource=admin&tls=true&tlsAllowInvalidCertificates=true";
        String uri = "mongodb://admin:Passw0rd@192.168.12.76:31172/admin?authSource=admin";
        return MongoClients.create(uri);
    }

    public static MongoClient getClient2() {
        MongoCredential credential = MongoCredential.createCredential(user, databaseName, password.toCharArray());
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToSslSettings(builder -> {
                            builder.enabled(false);
                            builder.invalidHostNameAllowed(true);
//                            builder.context(new SSLContext())
//                            builder.applyConnectionString(new ConnectionString())
                        })
                        .applyToClusterSettings(builder ->
                                builder.hosts(Arrays.asList(
                                        new ServerAddress("192.168.12.76", 31170),
                                        new ServerAddress("192.168.12.76", 31171),
                                        new ServerAddress("192.168.12.76", 31172)

                                )))
                        .credential(credential)
                        .build());
        return mongoClient;
    }
}