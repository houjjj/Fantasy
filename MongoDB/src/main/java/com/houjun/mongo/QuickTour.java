//package com.houjun.mongo;
//
//import static com.mongodb.client.model.Filters.eq;
//import static com.mongodb.client.model.Filters.gte;
//import static com.mongodb.client.model.Filters.lt;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import com.mongodb.*;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCursor;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.model.Filters;
//import com.mongodb.client.result.DeleteResult;
//import com.mongodb.client.result.UpdateResult;
//
//public class QuickTour {
//    static String databaseName = "houjuntest";
//    static String collectionName = "xinshu";
//    static MongoCollection<Document> firstCollection;
//
//    public static void main(String[] args) {
//        //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
//        //ServerAddress()两个参数分别为 服务器地址 和 端口
//        ServerAddress serverAddress = new ServerAddress("192.168.12.76", 30037);
//        List<ServerAddress> addrs = new ArrayList<>();
//        addrs.add(serverAddress);
//
//        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
//        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "Passw0rd".toCharArray());
//        List<MongoCredential> credentials = new ArrayList<>();
//        credentials.add(credential);
//
//        //通过连接认证获取MongoDB连接
//        MongoClientOptions options = MongoClientOptions.builder()
//                .maxWaitTime(30000)
//                .serverSelectionTimeout(30000)
//                .connectTimeout(39999)
//                .sslInvalidHostNameAllowed(false)
//                .build();
//        MongoClient mongoClient = new MongoClient(addrs, credentials, options);
////        MongoClient mongoClient = new MongoClient("192.168.20.105", 27017);
//        System.out.println(mongoClient.getReplicaSetStatus());
//        Document document = new Document("name", "MongoDB")
//                .append("type", "database")
//                .append("count", 1)
//                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
//                .append("info", new Document("x", 203).append("y", 102));
//        MongoDatabase database = mongoClient.getDatabase(databaseName);
//        MongoCollection<Document> collection = database.getCollection(collectionName);
//        //检索所有文档
//        /**
//         * 1. 获取迭代器FindIterable<Document>
//         * 2. 获取游标MongoCursor<Document>
//         * 3. 通过游标遍历检索出的文档集合
//         * */
//        FindIterable<Document> findIterable = collection.find();
//        for (Document value : findIterable) {
//            System.out.println(value);
//        }
//
//    }
////    public static void main(String[] args) {
////        QuickTour  quickTour = new QuickTour();
////        quickTour.setUp();
//////        quickTour.updateMultipleDoc();
//////        quickTour.deleteMultiple();
////
////        quickTour.insertDocument();
////    }
//
//
//    /**
//     * 连接数据库
//     */
//    public void setUp() {
//        MongoDBHelper.connect(databaseName);
//        firstCollection = MongoDBHelper.getCollection(collectionName);
//    }
//
//    public void getAllDocuments() {
//        MongoDBHelper.getAllDocuments(firstCollection);
//    }
//
//    public void getDocumentFirst() {
//        MongoDBHelper.connect(databaseName);
//        MongoCollection<Document> collection = MongoDBHelper.getCollection(collectionName);
//        String json = MongoDBHelper.getDocumentFirst(collection);
//        System.out.println(json);
//    }
//
//    /**
//     * 返回符合匹配条件的第一个doc
//     */
//    public void getDocMatcheFilter() {
//        Document myDoc = firstCollection.find(eq("y", 2)).first();
//        System.out.println(myDoc.toJson());
//    }
//
//    public void getAllDocMatcheFilter() {
//        firstCollection.find(Filters.gt("i", 10)).forEach((Block<? super Document>) (Document document) -> {
//            System.out.println(document.toJson());
//        });
//    }
//
//    public void updateOneDoc() {
//        UpdateResult updateResult = firstCollection.updateOne(Filters.eq("i", 12), new Document("$set", new Document("i", 21)));
//        System.out.println(updateResult.getModifiedCount());
//    }
//
//    public void updateMultipleDoc() {
//        UpdateResult updateResult = firstCollection.updateMany(lt("i", 100), new Document("$inc", new Document("i", 100)));
//        System.out.printf("count:%s,insertedId:%s", updateResult.getModifiedCount(), updateResult.getUpsertedId());
//        System.out.println();
//    }
//
//    public void deleteOne() {
//        DeleteResult deleteResult = firstCollection.deleteOne(eq("i", 121));
//        System.out.println(deleteResult.getDeletedCount());
//    }
//
//    public void deleteMultiple() {
//        DeleteResult deleteResult = firstCollection.deleteMany(gte("i", 100));
//        System.out.println(deleteResult.getDeletedCount());
//    }
//
//    /**
//     * 插入一个doc
//     */
//    public void insertDocument() {
//        Document doc = new Document("name", "MongoDB")
//                .append("type", "database")
//                .append("count", 1)
//                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
//                .append("info", new Document("x", 203).append("y", 102));
//        MongoDBHelper.insertDocument(firstCollection, doc);
//    }
//
//    /**
//     * 插入多个doc
//     */
//    public void insertMultipleDoc() {
//        List<Document> documents = new ArrayList<Document>();
//        for (int i = 0; i < 100; i++) {
//            documents.add(new Document("i", i));
//        }
//
//        MongoDBHelper.insertManyDocument(firstCollection, documents);
//    }
//
//    public void countDocs() {
//        System.out.println(firstCollection.getNamespace().getCollectionName() + "-count:" + firstCollection.count());
//    }
//
//}