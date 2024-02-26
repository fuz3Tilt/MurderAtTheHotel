package ru.kradin.murder_at_the_hotel.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Player {
    @Id
    private long chatId;
    @Column(unique = true, nullable = false)
    private String nickname;
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime registeredAt;

    public Player() {
    }

    public Player(long chatId, String nickname) {
        this.chatId = chatId;
        this.nickname = nickname;
        this.registeredAt = getCurrentTime();
    }

    public long getChatId() {
        return chatId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    private static LocalDateTime getCurrentTime() {
        LocalDateTime utcTime = LocalDateTime.now(ZoneId.of("UTC"));
        ZoneId timeZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime zonedDateTime = utcTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(timeZone);
        return zonedDateTime.toLocalDateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getChatId() == player.getChatId() && Objects.equals(getNickname(), player.getNickname()) && Objects.equals(getRegisteredAt(), player.getRegisteredAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChatId(), getNickname(), getRegisteredAt());
    }
}
