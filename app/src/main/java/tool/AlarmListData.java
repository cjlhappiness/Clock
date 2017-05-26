package tool;

public class AlarmListData {

    private int id;
    private int time;
    private String repeat;
    private int vibrate;
    private int ring;
    private int state;

    public AlarmListData(int id, int time, String repeat, int vibrate, int ring, int state) {
        this.id = id;
        this.time = time;
        this.repeat = repeat;
        this.vibrate = vibrate;
        this.ring = ring;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getRepeat() {
        return repeat;
    }

    public int getVibrate() {
        return vibrate;
    }

    public int getRing() {
        return ring;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
