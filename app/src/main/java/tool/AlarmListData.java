package tool;

public class AlarmListData{

    private int id;
    private int time;
    private String repeat;
    private int vibrate;
    private int ring;
    private int state;

    public AlarmListData( int id, int time, String repeat, int vibrate, int ring, int state){
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

    public void setTime(int time) {
        this.time = time;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setRing(int vibrate) {
        this.ring = ring;
    }

    public int getRing() {
        return ring;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
