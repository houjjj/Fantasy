package com.houjun.elasticsearch.helloworld.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
public class EsController {

    @Autowired
    private RestHighLevelClient client;

    @RequestMapping("/createIndex")
    public Boolean createIndex(String indexName) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.mapping(
                "{\n" +
                        "  \"properties\": {\n" +
                        "    \"message\": {\n" +
                        "      \"type\": \"text\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                XContentType.JSON);
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequestMapping("/deleteIndex")
    public Boolean deleteIndex(String indexName){
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        try {
            client.indices().delete(deleteIndexRequest ,RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    @RequestMapping("/existsIndex")
    public Boolean existsIndex(String indexName) {
        GetIndexRequest request = new GetIndexRequest(indexName);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    @RequestMapping("/closeIndex")
    public Boolean closeIndex(String indexName) {
        CloseIndexRequest closeIndexRequest = new CloseIndexRequest(indexName);
        try {
            client.indices().close(closeIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @RequestMapping("/openIndex")
    public Boolean openIndex(String indexName) {
        OpenIndexRequest openIndexRequest = new OpenIndexRequest(indexName);
        try {
            client.indices().open(openIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @RequestMapping("/addAlias")
    public Boolean addAlias(String indexName, String aliasName) {
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions aliasActions = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD);
        aliasActions.index(indexName).alias(aliasName);
        indicesAliasesRequest.addAliasAction(aliasActions);
        try {
            client.indices().updateAliases(indicesAliasesRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @RequestMapping("/removeAlias")
    public Boolean removeAlias(String indexName, String aliasName) {
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions aliasActions = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE);
        aliasActions.index(indexName).alias(aliasName);
        indicesAliasesRequest.addAliasAction(aliasActions);
        try {
            client.indices().updateAliases(indicesAliasesRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @RequestMapping("/changeAlias")
    public Boolean changeAlias() {
        String aliasName = "indexname_alias";
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions addAliasActions = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD);
        addAliasActions.index("indexname1").alias(aliasName);
        IndicesAliasesRequest.AliasActions removeAliasActions = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE);
        removeAliasActions.index("indexname2").alias(aliasName);
        indicesAliasesRequest.addAliasAction(addAliasActions);
        indicesAliasesRequest.addAliasAction(removeAliasActions);
        try {
            client.indices().updateAliases(indicesAliasesRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    @RequestMapping("/selectIndexByAlias")
    public Map selectIndexByAlias(String aliasName) {
        GetAliasesRequest getAliasesRequest = new GetAliasesRequest(aliasName);
        try {
            GetAliasesResponse response = client.indices().getAlias(getAliasesRequest,RequestOptions.DEFAULT);
            Map<String, Set<AliasMetadata>> aliases;
            aliases = response.getAliases();
            return aliases;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping("/getAliasExist")
    public Boolean getAliasExist(String indexName, String aliasName) {
        GetAliasesRequest getAliasesRequest = new GetAliasesRequest(aliasName);
        getAliasesRequest.indices(indexName);
        try {
            return client.indices().existsAlias(getAliasesRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
