package ElevatorSimulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ElevatorBoard {
    //����ÿһ�㰴ť
    public List<Stack<Client>> innerQueueArrayList = Collections.synchronizedList(new ArrayList<>(8)); //ÿһ������ڲ��ĳ˿�ջ���Ƚ����ĺ��ȥ��

    ElevatorBoard() {
        for (int i = 0; i < 9; i++) {
            Stack<Client> stack = new Stack<>();
            innerQueueArrayList.add(i, stack);
        }
    }
}
