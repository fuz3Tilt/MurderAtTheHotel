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

    @Override
    public String toString() {
        return "\uD83C\uDD94: "+"<code>"+id+"</code>"+"\n" +
                "\uD83D\uDC51: <i><b>"+owner.getNickname()+"</b></i>\n" +
                "\uD83C\uDFAE: <i><b>"+getPlayersString()+"</b></i>\n\n" +
                roomSettings.toString();
    }

    private void setId() {
        String preId = IdGenerator.generate();
        while(roomObserver.isRoomIdInUse(preId)) {
            preId = IdGenerator.generate();
        }
        id = preId;
    }

    private String getPlayersString() {
        StringBuilder playersToString = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            playersToString.append(players.get(i).getNickname());
            if (i != players.size() - 1) {
                playersToString.append(", ");
            }
        }
        return playersToString.toString();
    }
}
