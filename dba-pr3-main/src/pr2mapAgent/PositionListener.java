package pr2mapAgent;

public interface PositionListener {
    void onPositionUpdated(int[] oldPos, int[] currentPos, boolean targetReached, int energy);
}
