package ru.kradin.game.room;

import java.util.Objects;

public class RoomSettings {
    private AccessType accessType;
    private SpeedType speedType;
    private VotingType votingType;

    public RoomSettings(AccessType accessType, SpeedType speedType, VotingType votingType) {
        this.accessType = accessType;
        this.speedType = speedType;
        this.votingType = votingType;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public SpeedType getSpeedType() {
        return speedType;
    }

    public VotingType getVotingType() {
        return votingType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomSettings)) return false;
        RoomSettings that = (RoomSettings) o;
        return getAccessType() == that.getAccessType() && getSpeedType() == that.getSpeedType() && getVotingType() == that.getVotingType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccessType(), getSpeedType(), getVotingType());
    }

    @Override
    public String toString() {
        return accessType.emoji+": <i>"+accessType.type+"</i>\n" +
                speedType.emoji+": <i>"+speedType.type+"</i>\n" +
                votingType.emoji+": <i>"+votingType.type+"</i>";
    }

    public enum AccessType{
        PUBLIC("Открытая комната"),PRIVATE("Закрытая комната");
        private final String type;
        private final String emoji;
        AccessType(String type) {
            this.type = type;
            if (type.equals("Открытая комната"))
                emoji = "\uD83D\uDD13";
            else
                emoji = "\uD83D\uDD12";
        }
        public String getType(){
            return type;
        }

        public String getEmoji() {
            return emoji;
        }
    };

    public enum SpeedType{
        NORMAL("Нормальная скорость"),FAST("Быстрая скорость");
        private final String type;
        private final String emoji;
        SpeedType(String type) {
            this.type = type;
            if (type.equals("Нормальная скорость"))
                emoji = "\uD83E\uDD8D";
            else
                emoji = "\uD83D\uDC06";
        }
        public String getType(){
            return type;
        }

        public String getEmoji() {
            return emoji;
        }
    };

    public enum VotingType{
        SECRET("Закрытое голосование"),PUBLIC("Открытое голосование");
        private final String type;
        private final String emoji;
        VotingType(String type) {
            this.type = type;
            if (type.equals("Закрытое голосование"))
                emoji = "\uD83E\uDD77:";
            else
                emoji = "\uD83D\uDC6E";
        }
        public String getType(){
            return type;
        }
        public String getEmoji() {
            return emoji;
        }
    };
}
