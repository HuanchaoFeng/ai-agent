package com.yupi.yuaiagent.tool;


import org.springframework.ai.tool.annotation.Tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalOperationTool {

    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(String command) {

        StringBuilder output = new StringBuilder();
        try {
            //添加指令
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = builder.start();//启动
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"))) {
                String line; //读取输出
                while((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            int exitCode = process.waitFor(); //退出码
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }

        }catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());

        }

        return output.toString();

    }
}
