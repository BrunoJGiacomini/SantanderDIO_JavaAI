package com.example.budgeting;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.budgeting.application.FinancialSummaryUseCase;
import com.example.budgeting.application.ListTransactionsByCategoryUseCase;
import com.example.budgeting.application.PersistTransactionUseCase;

@RestController
@RequestMapping("/api")
public class ChatClientController {
    private final ChatClient chatClient;
    private final FinancialSummaryUseCase financialSummaryUseCase;
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;

    public ChatClientController(
            ChatClient chatClient,
            FinancialSummaryUseCase financialSummaryUseCase,
            PersistTransactionUseCase persistTransactionUseCase,
            ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase) {

        this.chatClient = chatClient;
        this.financialSummaryUseCase = financialSummaryUseCase;
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
    }

    @GetMapping("/chat")
    String chat(String prompt) {
        return this.chatClient
                .prompt()
                .user(prompt)
                .tools(
                        financialSummaryUseCase,
                        persistTransactionUseCase,
                        listTransactionsByCategoryUseCase)
                .call()
                .content();
    }
}
