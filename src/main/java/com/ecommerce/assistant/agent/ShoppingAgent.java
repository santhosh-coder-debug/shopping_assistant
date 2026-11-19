package com.ecommerce.assistant.agent;
import com.ecommerce.assistant.tool.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
@Service
public class ShoppingAgent {
 private final ChatClient client;private final ProductSearchTools search;private final ProductComparisonTools compare;
 public ShoppingAgent(ChatClient shoppingChatClient,ProductSearchTools search,ProductComparisonTools compare){this.client=shoppingChatClient;this.search=search;this.compare=compare;}
 public String chat(String conversationId,String message){return client.prompt().user(message).tools(search,compare).advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId)).call().content();}
}
