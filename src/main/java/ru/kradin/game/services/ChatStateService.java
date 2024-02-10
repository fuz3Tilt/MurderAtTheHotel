package ru.kradin.game.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kradin.game.enums.SpecialLocalState;
import ru.kradin.game.handlers.MainMenuHandler;
import ru.kradin.game.handlers.RegistrationHandler;
import ru.kradin.game.models.ChatState;
import ru.kradin.game.repositories.ChatStateRepository;

import java.util.Optional;

@Service
public class ChatStateService {
    @Autowired
    ChatStateRepository chatStateRepository;

    public String getStateByChatId(long chatId) {
        Optional<ChatState> chatStateOptional = chatStateRepository.findById(chatId);
        String state;
        if (chatStateOptional.isEmpty()) {
            ChatState chatState = new ChatState(chatId, RegistrationHandler.getStateForEntering());
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
