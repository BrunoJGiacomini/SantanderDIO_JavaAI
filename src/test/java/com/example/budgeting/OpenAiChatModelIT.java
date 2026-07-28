package com.example.budgeting;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
// O teste só roda se a variável de ambiente OPENAI_API_KEY existir e não for vazia
// Isso evita falha de build em CI/local sem a chave configurada
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatModelIT {
    // Injeta o bean OpenAiApi configurado automaticamente pelo Spring AI
    // (usa a API key e demais configs do application.properties/yml)
    @Autowired
    OpenAiApi openAiApi;



    @Test
    void should_receiveResponse_when_chatModelIsCalled(){
        // Configurações da chamada ao modelo: qual modelo usar, "criatividade" (temperature)
        // e o formato esperado da resposta (texto puro, sem JSON estruturado)
        var options = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.8)
                .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.TEXT).build())
                .build();

        // Monta o ChatModel usando a API já autenticada + as opções acima como padrão
        var chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();

        
        var response = chatModel.call("Gere um registro de budgeting, com descrição de gasto, valor em reais e local");

        assertThat(response).isNotEmpty();
        System.out.println(response);
    }
}
