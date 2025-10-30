package com.yupi.yuimagesearchmcpservers.tool;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageSearchTool {

    private static final String API_KEY = "dJWVDpZn32YHuvW9cB5biKSD1bRFYS1DrnevTg5kEm7LBBWwa8XK2prn";
    private static final String API_URL = "https://api.pexels.com/v1/search";

    @Tool(description = "search image from web")
    public String searchImageFromWeb(@ToolParam(description = "search query keyword") String searchText) {
        try{
            return String.join(",", searchMediumImage(searchText));
        }catch (Exception e){
            return "Error search image" + e.getMessage();
        }

    }

    public List<String> searchMediumImage(String query){
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", API_KEY);
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);

        String response = HttpUtil.createGet(API_URL)
                .addHeaders(headers)
                .form(params)
                .execute().body();

        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(photoObj -> (JSONObject) photoObj)
                .map(photoObj -> photoObj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}
