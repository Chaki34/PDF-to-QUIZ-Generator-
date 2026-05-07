package PDFquizAI.com.PDFquizAI.Config;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;





@Configuration
public class OpenRouterConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .build();
    }

    @Bean
    public OpenAiChatOptions openAiChatOptions() {
        return OpenAiChatOptions.builder()
                .model("meta-llama/llama-3.1-8b-instruct")
                .temperature(0.7)
                .maxTokens(2500) // 🔥 IMPORTANT FIX
                .build();
    }
}