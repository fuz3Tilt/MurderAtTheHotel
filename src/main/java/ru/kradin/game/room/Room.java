package ru.kradin.game.room;

import ru.kradin.game.models.Player;
import ru.kradin.game.utils.IdGenerator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
        roomsObserver.roomCreated(this);
    }

    public void addPlayer(Player player) {
        players.add(player);
        roomsObserver.playerAdded(player.getChatId(), id);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (owner.equals(player) && players.size()!=0) {
            owner = players.get(0);
        }
        roomsObserver.playerRemoved(player.getChatId(), id);
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

    private void setId() {
        String preId = IdGenerator.generate();
        while(roomsObserver.isRoomIdInUse(preId)) {
            preId = IdGenerator.generate();
        }
        id = preId;
    }
}
