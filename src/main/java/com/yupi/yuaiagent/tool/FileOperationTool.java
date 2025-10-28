package com.yupi.yuaiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.yupi.yuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "name of the file to read") String fileName){
        String filePath = FILE_DIR + "/" + fileName;
        try{
            return FileUtil.readUtf8String(filePath);
        }catch (Exception e){
            return "Error reading file: " + e.getMessage();
        }
    }


    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "name of the file to write") String fileName, @ToolParam(description = "Content to write to the file") String content){
        String filePath = FILE_DIR + "/" + fileName;
        try {
            FileUtil.mkdir(filePath);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully write to: " + filePath;
        } catch (Exception e) {
            return "Error writing file: " + e.getMessage();
        }
    }

}
