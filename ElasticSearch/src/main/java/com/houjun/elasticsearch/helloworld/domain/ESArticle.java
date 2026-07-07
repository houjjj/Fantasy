package com.houjun.elasticsearch.helloworld.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "article")
@EqualsAndHashCode(callSuper = false)
public class ESArticle   implements Serializable {
    /**
     * 自定义时间格式
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 唯一标识 id
     */
    @Id
    @Field(type = FieldType.Text)
    private String id;

    /**
     * 标题，字段类型为 Text，没有 String 类型；分词类型为 ik 分词器的最细颗粒度划分法。
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    /**
     * 内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    @Field(type = FieldType.Text)
    private String userId;

    /**
     * 创建时间，单独存储，字段类型为 Date ，自定义格式
     */
    @Field(store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间，单独存储，字段类型为 Date ，自定义格式
     */
    @Field(store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;
}
