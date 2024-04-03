package com.cqupt.software_1.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HTTPUtils {
    public static final String rootPath = "http://localhost:5000/";
    public static JsonNode postRequest(Object paramData, String path) throws URISyntaxException, IOException {
        URI uri = new URI(rootPath+path);
        // 创建http POST
        HttpPost httpPost = new HttpPost(uri);
        HttpClient httpClient = HttpClients.createDefault();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        String jsonData = objectMapper.writeValueAsString(paramData);

//        httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");
//        httpPost.setEntity(new StringEntity(jsonData)); // 设置请求体
        // 设置请求体编码为 UTF-8
        StringEntity entity = new StringEntity(jsonData, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8"); // 设置请求头部


        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        String responseData = EntityUtils.toString(response.getEntity());

        JsonNode jsonNode = objectMapper.readValue(responseData, JsonNode.class);
        return jsonNode;
    }
}
