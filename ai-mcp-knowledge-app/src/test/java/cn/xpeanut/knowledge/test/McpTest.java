package cn.xpeanut.knowledge.test;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class McpTest {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Resource
    private ToolCallbackProvider tools;

    @Test
    public void test_tools() {
        String userInput = "有哪些工具可以使用";
        ChatClient chatClient = chatClientBuilder.defaultTools(tools)
                .defaultOptions(OllamaOptions.builder().model("qwen3").build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(new Prompt(userInput)).call().content());
    }

    @Test
    public void test_computer_tools() {
        String userInput = "获取电脑配置 在 C:\\\\Users\\\\Administrator\\\\Desktop\\\\ 文件夹下，创建 电脑.txt 把电脑配置写入 电脑.txt";
        ChatClient chatClient = chatClientBuilder.defaultTools(tools)
                .defaultOptions(OllamaOptions.builder().model("qwen3").build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

}
