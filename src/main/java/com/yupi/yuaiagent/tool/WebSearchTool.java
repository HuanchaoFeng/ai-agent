package com.yupi.yuaiagent.tool;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebSearchTool {

    private static final String SEARCH_API_URP = "https://www.searchapi.io/api/v1/search";
    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 示例：
     * OkHttpClient client = new OkHttpClient();
     *
     * HttpUrl.Builder urlBuilder = HttpUrl.get("https://www.searchapi.io/api/v1/search").newBuilder();
     * urlBuilder.addQueryParameter("engine", "baidu");
     * urlBuilder.addQueryParameter("q", "ERNIE Bot");
     *
     * Request request = new Request.Builder()
     *   .url(urlBuilder.build())
     *   .build();
     *
     * Response response = client.newCall(request).execute();
     * System.out.println(response.body().string());
     */

    @Tool(description = "Search for information from Baidu seatch Engine")
    public String searchWeb(@ToolParam(description = "Search query keyword") String query){
        Map<String, Object> map = new HashMap<>();
        map.put("q", query);
        map.put("api_key", apiKey);
        map.put("engine", "baidu"); //指定baidu搜索引擎
        try{
            String response = HttpUtil.get(SEARCH_API_URP, map);
            JSONObject jsonObject = JSONUtil.parseObj(response);
            //取出organic_results(存放结果的地方)的前5条结果,api示例
            JSONArray organicResult = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResult.subList(0, 5);

            //把每条结果转为 JSON 字符串
            String result = objects.stream().map(obj -> {
                JSONObject tempObject = (JSONObject) obj;
                return tempObject.toString();
            }).collect(Collectors.joining(","));

            return result;

        }catch (Exception e){
            return "Error searching Baidu: " + e.getMessage();

        }


    }
}
