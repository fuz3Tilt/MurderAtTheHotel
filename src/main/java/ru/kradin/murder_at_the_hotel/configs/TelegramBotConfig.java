package ru.kradin.murder_at_the_hotel.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {
    @Value("${telegramBot.username}")
    private String username;
    @Value("${telegramBot.token}")
    private String token;

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}
