package ru.kradin.game.room;

import ru.kradin.game.enums.RoomNotifyType;
import ru.kradin.game.enums.RoomType;
import ru.kradin.game.models.Player;
import ru.kradin.game.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Room {
    private String id;
    private Player owner;
    private List<Player> players = new ArrayList<>();
    private RoomsObserver roomsObserver;
    private RoomType roomType;

    Room(RoomsObserver roomsObserver, Player owner, RoomType roomType) {
        this.owner = owner;
        this.roomsObserver = roomsObserver;
        players.add(owner);
        this.roomType = roomType;
        setId();
        roomsObserver.notify(this, RoomNotifyType.ROOM_CREATED);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (owner.equals(player) && players.size()!=0) {
            owner = players.get(0);
        }
        roomsObserver.notify(this,RoomNotifyType.PLAYER_LEFT);
    }

    public String getId() {
        return id;
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * Возвращает копию List
     * @return
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public boolean isPublic() {
        return roomType.equals(RoomType.PUBLIC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(getId(), room.getId()) && Objects.equals(getOwner(), room.getOwner()) && Objects.equals(getPlayers(), room.getPlayers()) && roomType == room.roomType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getPlayers(), roomType);
    }

    @Override
    public String toString() {
        return "⚙\uFE0F: "+roomType.getType()+"\n" +
                "\uD83C\uDD94: "+"<code>"+id+"</code>"+"\n" +
                "\uD83D\uDC51: "+owner.getNickname()+"\n" +
                "\uD83C\uDFAE: "+getPlayersString();
    }

    private void setId() {
        String preId = IdGenerator.generate();
        while(roomsObserver.isRoomIdInUse(preId)) {
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
