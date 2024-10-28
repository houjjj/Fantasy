package com.houjun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper objectMapper;
    static {
        // todo use objectmapper spring provided for consistent behavior of serialization and deserialization
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        objectMapper = builder
                // fix Can not construct instance of java.time.LocalDateTime: no suitable constructor found, can not deserialize from Object value        .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .serializers(
                        new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .deserializers(
                        new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static <T> T toObject(Class<T> type, String json) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("json error", e);
            return null;
        }
    }

    public static <T> String toJson(T t)  {
        if (t == null) return null;
        try {
            if (t instanceof String) return (String)t;
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(JsonUtil.toJson("shindb"));
    }

    public static <T> T toObject(String json, TypeReference<T> type){
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            log.error("json error", e);
            return null;
        }
    }

    public static Map toMap(Object t){
        return objectMapper.convertValue(t, Map.class);
    }

    // todo add test for objectmapper configuration working correct
}
