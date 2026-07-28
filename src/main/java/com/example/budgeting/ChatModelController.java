package com.example.budgeting;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api")
public class ChatModelController {
    // Injeta o modelo de chat da OpenAI (configurado via application.properties/yml)
    private final OpenAiChatModel openAiChatModel;

    // Injeção de dependência via construtor — o Spring cria e injeta o bean automaticamente
    public ChatModelController(OpenAiChatModel openAiChatModel){
        this.openAiChatModel = openAiChatModel;
    }


    // Endpoint GET em /api/chat-model
    // Recebe um "prompt" como parâmetro (query param, ex: ?prompt=oi)
    // e retorna a resposta gerada pelo modelo da OpenAI como texto puro
    @GetMapping("/chat-model")
    String chat(String prompt) {
        return this.openAiChatModel.call(prompt);
    }
    


}
