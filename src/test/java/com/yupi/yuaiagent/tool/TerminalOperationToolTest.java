package com.yupi.yuaiagent.tool;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TerminalOperationToolTest {
    @Test
    public void testTerminalOperationTool() {
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        String command = "dir";
        String result = terminalOperationTool.executeTerminalCommand(command);
        Assertions.assertNotNull(result);
    }

}
