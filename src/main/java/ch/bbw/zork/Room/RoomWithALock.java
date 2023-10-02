package ch.bbw.zork.Room;

public class RoomWithALock extends Room {

    private boolean isLocked;

    public RoomWithALock(String description) {
        super(description);
        this.isLocked = true;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void unlockRoom() {
        isLocked = true;
    }
}
