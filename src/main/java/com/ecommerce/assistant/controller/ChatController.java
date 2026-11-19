package com.ecommerce.assistant.controller;
import com.ecommerce.assistant.agent.ShoppingAgent;
import com.ecommerce.assistant.dto.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/chat")
public class ChatController {
 private final ShoppingAgent agent;
 public ChatController(ShoppingAgent agent){this.agent=agent;}
 @PostMapping public ChatResponse chat(@Valid @RequestBody ChatRequest req){return new ChatResponse(req.conversationId(),agent.chat(req.conversationId(),req.message()));}
}
