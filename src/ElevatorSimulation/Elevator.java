package ElevatorSimulation;

import static ElevatorSimulation.ElevatorMove.Waiting;
import static ElevatorSimulation.ElevatorState.UP;

/**
 *电梯类
 */
public class Elevator {
    public static int clientNum; //电梯内乘客的数量，最多为13人
    public ElevatorState elevatorState = UP; //电梯的移动方向
    public ElevatorMove elevatorMove = Waiting; //电梯的移动状态
    public int floor; //电梯所在楼层
    public long time;
    public boolean isFull = false;
    public Elevator(int clientNum, ElevatorState elevatorState, ElevatorMove elevatorMove, int floor, int time) {
        this.clientNum = clientNum;
        this.elevatorState = elevatorState;
        this.elevatorMove = elevatorMove;
        this.floor = floor;
        this.time = time;
    }
}
