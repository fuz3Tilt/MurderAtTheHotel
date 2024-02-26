package ru.kradin.murder_at_the_hotel.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kradin.murder_at_the_hotel.exceptions.PlayerDoesNotExistException;
import ru.kradin.murder_at_the_hotel.models.Player;
import ru.kradin.murder_at_the_hotel.repositories.PlayerRepository;

import java.util.Optional;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public Player getByChatId(long chatId) throws PlayerDoesNotExistException {
        Optional<Player> playerOptional = playerRepository.findById(chatId);

        if (playerOptional.isEmpty())
            throw new PlayerDoesNotExistException();

        return playerOptional.get();
    }

    public boolean isNicknameUses(String nickname) {
        Optional<Player> playerOptional = playerRepository.findByNicknameIgnoreCase(nickname);
        return playerOptional.isPresent();
    }

    public void changeNickname(long chatId, String nickname) {
        Player player = playerRepository.findById(chatId).get();
        player.setNickname(nickname);
        playerRepository.save(player);
    }

    public void register(long chatId, String nickname) {
        Player player = new Player(chatId, nickname);
        playerRepository.save(player);
    }
}
