package ru.kradin.murder_at_the_hotel.room;

import ru.kradin.murder_at_the_hotel.enums.RoomNotifyType;
import ru.kradin.murder_at_the_hotel.models.Player;
import ru.kradin.murder_at_the_hotel.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Room {
    private String id;
    private Player owner;
    private List<Player> players = new ArrayList<>();
    private RoomObserver roomObserver;
    private RoomSettings roomSettings;

    Room(RoomObserver roomObserver, Player owner, RoomSettings roomSettings) {
        this.owner = owner;
        this.roomObserver = roomObserver;
        players.add(owner);
        this.roomSettings = roomSettings;
        setId();
        roomObserver.notify(this, RoomNotifyType.ROOM_CREATED);
    }

    public void setNotSearchable() {
        roomObserver.notify(this,RoomNotifyType.NOT_SEARCHABLE);
    }

    public void setSearchable() {
        roomObserver.notify(this,RoomNotifyType.SEARCHABLE);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (owner.equals(player) && players.size()!=0) {
            owner = players.get(0);
        }
        roomObserver.notify(this,RoomNotifyType.PLAYER_LEFT);
    }

    public RoomSettings getRoomSettings() {
        return roomSettings;
    }

    public String getId() {
        return id;
    }

    public Player getOwner() {
        return owner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isPublic() {
        return roomSettings.getAccessType().equals(RoomSettings.AccessType.PUBLIC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(getId(), room.getId()) && Objects.equals(roomSettings, room.roomSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), roomSettings);
    }

    public String getInfo(long currentPlayerChatId) {
        StringBuilder roomInfoBuilder = new StringBuilder();
        roomInfoBuilder.append("\uD83C\uDD94: <code>").append(id).append("</code>\n");
        roomInfoBuilder.append("\uD83D\uDC51: ");

        if (owner.getChatId() == currentPlayerChatId) {
            roomInfoBuilder.append("<i><b>").append(owner.getNickname()).append("</b></i>\n");
        } else {
            roomInfoBuilder.append(owner.getNickname()).append("\n");
        }

        roomInfoBuilder.append("\uD83C\uDFAE: ");

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            if (player.getChatId() == currentPlayerChatId) {
                roomInfoBuilder.append("<i><b>").append(player.getNickname()).append("</b></i>");
            } else {
                roomInfoBuilder.append(player.getNickname());
            }

            if (i != players.size() - 1) {
                roomInfoBuilder.append(", ");
            }
        }

        roomInfoBuilder.append("\n\n");

        roomInfoBuilder.append(roomSettings.toString());

        return roomInfoBuilder.toString();
    }

    private void setId() {
        String preId = IdGenerator.generate();
        while(roomObserver.isRoomIdInUse(preId)) {
            preId = IdGenerator.generate();
        }
        id = preId;
    }
}
