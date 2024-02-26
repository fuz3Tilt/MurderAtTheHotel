package ru.kradin.murder_at_the_hotel.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kradin.murder_at_the_hotel.handlers.RegistrationHandler;
import ru.kradin.murder_at_the_hotel.models.ChatState;
import ru.kradin.murder_at_the_hotel.repositories.ChatStateRepository;

import java.util.Optional;

@Service
public class ChatStateService {
    @Autowired
    private ChatStateRepository chatStateRepository;

    public String getStateByChatId(long chatId) {
        Optional<ChatState> chatStateOptional = chatStateRepository.findById(chatId);
        String state;
        if (chatStateOptional.isEmpty()) {
            ChatState chatState = new ChatState(chatId, RegistrationHandler.getStateForStartingRegistration());
            chatStateRepository.save(chatState);
            state = chatState.getState();
        } else {
            state = chatStateOptional.get().getState();
        }
        return state;
    }

    public String setState(long chatId, String state) {
        Optional<ChatState> chatStateOptional = chatStateRepository.findById(chatId);
        ChatState chatState;
        if (chatStateOptional.isEmpty()) {
            chatState = new ChatState(chatId, state);
        } else {
            chatState = chatStateOptional.get();
            chatState.setState(state);
        }
        chatStateRepository.save(chatState);
        return chatState.getState();
    }
}
