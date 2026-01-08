package cn.xpeanut.knowledge.test;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OllamaTest {

    @Value("classpath:/data/planet.jpg")
    private org.springframework.core.io.Resource imageResource;

    @Resource
    private OllamaChatModel ollamaChatModel;

    @Resource(name = "ollamaSimpleVectorStore")
    public SimpleVectorStore simpleVectorStore;

    @Resource(name = "ollamaPgVectorStore")
    public PgVectorStore pgVectorStore;

    @Resource
    public TokenTextSplitter tokenTextSplitter;

    @Test
    public void test_call() {
        ChatResponse chatResponse = ollamaChatModel.call(new Prompt("1+1", OpenAiChatOptions.builder()
                .model("deepseek-r1:1.5b").build()));
        log.info("测试结果：{}", JSON.toJSONString(chatResponse));
    }

    @Test
    public void test_call_images() {
        // 构建请求信息
        UserMessage userMessage = new UserMessage("请描述这张图片的主要内容", new Media(MimeType.valueOf(MimeTypeUtils.IMAGE_JPEG_VALUE), imageResource));
        ChatResponse chatResponse = ollamaChatModel.call(new Prompt(userMessage, OpenAiChatOptions.builder().model("deepseek-r1:1.5b").build()));

        log.info("测试结果(images)：{}", JSON.toJSONString(chatResponse));
    }

    @Test
    public void test_call_stream() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<ChatResponse> stream = ollamaChatModel.stream(new Prompt("1+1", OpenAiChatOptions.builder().model("deepseek-r1:1.5b").build()));
        stream.subscribe(
                chatResponse -> {
                    AssistantMessage output = chatResponse.getResult().getOutput();
                    log.info("测试结果(stream)：{}", JSON.toJSONString(output));
                },
                Throwable::printStackTrace,
                () -> {
                    countDownLatch.countDown();
                    log.info("测试结果(stream)：输出完成");
                }
        );

        countDownLatch.await();
    }

    @Test
    public void upload() {
        TikaDocumentReader reader = new TikaDocumentReader("./data/file.txt");

        List<Document> documents = reader.get();
        List<Document> documentSplitterList = tokenTextSplitter.apply(documents);

        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "知识库"));

        pgVectorStore.accept(documentSplitterList);

        log.info("上传完成！");
    }

    @Test
    public void test_rag_chat() {
        String message = "张三今年几岁";

        String SYSTEM_PROMPT = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;

        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(5)
                .filterExpression("knowledge == '知识库'")
                .build();

        List<Document> documents = pgVectorStore.similaritySearch(request);

        String documentsCollectors = null == documents ? "" : documents.stream().map(Document::getText).collect(Collectors.joining());

        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentsCollectors));

        ArrayList<Message> promptMessages = new ArrayList<>();
        promptMessages.add(new UserMessage(message));
        promptMessages.add(ragMessage);

        ChatResponse chatResponse = ollamaChatModel.call(new Prompt(promptMessages, OpenAiChatOptions.builder().model("deepseek-r1:1.5b").build()));

        log.info("测试结果(chat)：{}", JSON.toJSONString(chatResponse));
    }

}
