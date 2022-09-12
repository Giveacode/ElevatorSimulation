package ElevatorSimulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ElevatorBoard {
    //代表每一层按钮
    public List<Stack<Client>> innerQueueArrayList = Collections.synchronizedList(new ArrayList<>(8)); //每一层电梯内部的乘客栈，先进来的后出去。

    ElevatorBoard() {
        for (int i = 0; i < 9; i++) {
            Stack<Client> stack = new Stack<>();
            innerQueueArrayList.add(i, stack);
        }
    }
}
