package ElevatorSimulation;

import java.text.SimpleDateFormat;
import java.util.*;
import static java.lang.Thread.sleep;

public class Main {
    public static final int t = 100; // 100ms。
    public static int id = 1; // 总共生成的乘客数，用于乘客编号,每调用一次randomClient()，值就+1。

    public static boolean ischange = false; // 是否到达顶楼或一层。

    public static void main(String[] args) throws InterruptedException {

        ElevatorBoard elevatorBoard =new ElevatorBoard();

        // 创建用于存储各层用户的集合。
        List<LinkedList<Client>> waitQueueArrayList = Collections.synchronizedList(new ArrayList<>(8)); //每一次楼层中等待坐电梯的乘客队列，先请求的先坐电梯。

        // 为等待队列初始化。
        for (int i = 0; i < 9; i++) {
            LinkedList<Client> linkedList = new LinkedList<>();
            waitQueueArrayList.add(i, linkedList);
        }

        // 创建电梯对象。
        Elevator elevator = new Elevator(0, ElevatorState.UP, ElevatorMove.Waiting, 0, 0);

        // 用于计时电梯是否原地待命300s。
        int count = 0;

        // 创建时间对象。
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        elevator.time = System.currentTimeMillis();

        // 创建用于随机生成乘客的线程。
        CreateClientThread createClientThread = new CreateClientThread(waitQueueArrayList);
        createClientThread.start();

        // 电梯运动判断，每一层判断一次，及一个循环。
        while (true) {
            // 判断是否所有楼层都没有乘客进出，是则在现在楼层待命。
            if (isAllEmpty(-1, 9, waitQueueArrayList, elevatorBoard.innerQueueArrayList)) {
                // 判断电梯是否原地待命300s,是则置楼层为1.
                if (count == 6) {
                    System.out.println("电梯已等待300s，正在前往本垒层（1楼）。。。");
                    sleep((elevator.floor - 1) * 8000L);
                    elevator.time += (elevator.floor - 1) * 8000L;
                    elevator.floor = 1;
                }
                System.out.println("电梯正在" + (elevator.floor + 1) + "层等待。。。");
                sleep(5000);
                count++;
                continue;
            }
            // 判断刚刚是否改变了电梯运动方向，防止“电梯到达xx层”被输出两次。
            if (!ischange) {
                System.out.println();
                System.out.println("\t\t\t\t\t电梯到达第" + (elevator.floor + 1) + "层\t\t\t\t\t\t\t现在时间：" + sdf.format(elevator.time));
            } else {
                ischange = false;
            }
            // 判断电梯的运动方向 UP or DOWN。
            if (elevator.elevatorState == ElevatorState.UP) {
                // 开始每层的动作（乘客进出）。
                atFloorAction(waitQueueArrayList, elevatorBoard.innerQueueArrayList, elevator);
                // 判断目前方向上是否还有乘客要进出，没有则调头。
                if (isAllEmpty(elevator.floor, 9, waitQueueArrayList, elevatorBoard.innerQueueArrayList)) {
                    System.out.println("当前方向的楼层已经没有乘客进出，电梯正在调头。。。");
                    sleep(1000);
                    System.out.println("电梯正在下降。。。");
                    elevator.time += 99 * t;
                    sleep(99 * t);
                    elevator.elevatorState = ElevatorState.DOWN;
                    elevator.floor--;
                    continue;
                }
                // 判断是否到达顶层，是则调头。
                if (elevator.floor + 1 == 9) {
                    elevator.elevatorState = ElevatorState.DOWN;
                    elevator.floor--;
                    ischange = true;
                    continue;
                }
                // 输出正在上升，并把时间、楼层更新。
                System.out.println("电梯正在上升。。。");
                elevator.time += 80 * t;
                sleep(80 * t);
                elevator.floor++;
            } else if (elevator.elevatorState == ElevatorState.DOWN) {
                // 开始每层的动作（乘客进出）。
                atFloorAction(waitQueueArrayList, elevatorBoard.innerQueueArrayList, elevator);
                // 判断目前方向上是否还有乘客要进出，没有则调头。
                if (isAllEmpty(0, elevator.floor, waitQueueArrayList, elevatorBoard.innerQueueArrayList)) {
                    System.out.println("当前方向的楼层已经没有乘客进出，电梯正在调头。。。");
                    sleep(1000);
                    System.out.println("电梯正在上升。。。");
                    elevator.time += 80 * t;
                    sleep(80 * t);
                    elevator.elevatorState = ElevatorState.UP;
                    elevator.floor++;
                    continue;
                }
                // 判断是否到达顶层，是则调头。
                if (elevator.floor - 1 == -1) {
                    elevator.elevatorState = ElevatorState.UP;
                    elevator.floor++;
                    ischange = true;
                    continue;
                }
                // 输出正在上升，并把时间、楼层更新。
                System.out.println("电梯正在下降。。。");
                elevator.time += 99 * t;
                sleep(99 * t);
                elevator.floor--;
            }
            if (id == 500) break;
        }
    }

    /**
     * 电梯到达某层后调用，判断出入乘客
     *
     * @param waitQueueArrayList  各层等待数列的集合表
     * @param innerQueueArrayList 各层电梯内栈的集合表
     * @param elevator            电梯类
     * @throws InterruptedException 异常
     */
    private static void atFloorAction(List<LinkedList<Client>> waitQueueArrayList, List<Stack<Client>> innerQueueArrayList, Elevator elevator) throws InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isOutKaimen = false;
        boolean isAction = false;
        // 判断如果该层的电梯内栈有人出，则执行出乘客操作。
        if (innerQueueArrayList.get(elevator.floor).size() != 0) {
            isOutKaimen = true;
            sleep(20 * t);
            System.out.println("电梯已开门。。。" + "\t\t\t\t\t\t\t\t\t\t\t现在时间；" + sdf.format(elevator.time));
            outClient(innerQueueArrayList, elevator);
            isAction = true;
        }
        // 判断如果该层楼层等待队列有人进，则执行进乘客操作。
        if (waitQueueArrayList.get(elevator.floor).size() != 0) {
            if (!isOutKaimen) {
                sleep(20 * t);
                System.out.println("电梯已开门。。。" + "\t\t\t\t\t\t\t\t\t\t\t现在时间；" + sdf.format(elevator.time));
            }
            inClient(waitQueueArrayList, innerQueueArrayList, elevator);
            isAction = true;
        }
        // 每各4s循环判断电梯是否有乘客想进入。
        if (isAction) {
            while (true) {
                if (elevator.isFull) break;
                System.out.println("电梯正在判断是否有乘客进入。。。");
                sleep(4000);
                if (waitQueueArrayList.get(elevator.floor).isEmpty()) {
                    System.out.println("无人进出");
                    break;
                } else {
                    System.out.println("有乘客发出请求：");
                    inClient(waitQueueArrayList, innerQueueArrayList, elevator);
                }
            }
            sleep(20 * t);
            System.out.println("电梯已关门。。。" + "\t\t\t\t\t\t\t\t\t\t\t现在时间；" + sdf.format(elevator.time));
        }
    }

    /**
     * 乘客入电梯操作
     *
     * @param waitQueueArrayList  各层等待数列的集合表
     * @param innerQueueArrayList 各层电梯内栈的集合表
     * @param elevator            电梯类
     * @throws InterruptedException 异常
     */
    private static void inClient(List<LinkedList<Client>> waitQueueArrayList, List<Stack<Client>> innerQueueArrayList, Elevator elevator) throws InterruptedException {
        // 创建SimpleDateFormat对象做时间处理。
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 得到该层准备进电梯的人数。
        int size = waitQueueArrayList.get(elevator.floor).size();

        // 乘客开始进入电梯。
        for (int i = 0; i < size; i++) {
            // 每一次乘客进入电梯前第一步：判断电梯是否满员（13人），满员则该层乘客进入电梯的操作直接结束，return。
            if (Elevator.clientNum >= 13) {
                System.out.println("电梯人数已满，不再接收乘客。");
                elevator.isFull = true;
                return;
            }

            // 获取现在处理的乘客的唯一id。
            int id = waitQueueArrayList.get(elevator.floor).element().clientId;

            // 时间、总人数更新。
            Elevator.clientNum++;
            elevator.time += 25 * t;

            // 输出乘客进入电梯信息、时间、总人数。
            if(Elevator.clientNum >=10 ) System.out.println("乘客" + id + " (目标层：" + (waitQueueArrayList.get(elevator.floor).element().outFloor + 1)
                    + ") " + "进入了电梯\t\t\t电梯人数：" + Elevator.clientNum + "人" + "\t\t现在时间：" + sdf.format(elevator.time));
            else System.out.println("乘客" + id + " (目标层：" + (waitQueueArrayList.get(elevator.floor).element().outFloor + 1)
                    + ") " + "进入了电梯\t\t\t电梯人数：" + Elevator.clientNum + "人" + "\t\t\t现在时间：" + sdf.format(elevator.time));
            sleep(25 * t);

            // 将该层的等待数列中的乘客依次加入其乘客目标层的电梯内栈，用于出电梯时操作。
            innerQueueArrayList.get(waitQueueArrayList.get(elevator.floor).element().outFloor).add(waitQueueArrayList.get(elevator.floor).element());

            // 执行上一步后，将该层的等待数列中的乘客删除。
            waitQueueArrayList.get(elevator.floor).pop();
        }
    }

    /**
     * 乘客出电梯操作
     *
     * @param innerQueueArrayList 各层电梯内栈的集合表
     * @param elevator            电梯类
     * @throws InterruptedException 异常
     */
    private static void outClient(List<Stack<Client>> innerQueueArrayList, Elevator elevator) throws InterruptedException {
        // 创建SimpleDateFormat对象做时间处理。
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 得到该层准备进电梯的人数。
        int size = innerQueueArrayList.get(elevator.floor).size();

        // 乘客开始进入电梯。
        for (int i = 0; i < size; i++) {

            // 获取现在处理的乘客的唯一id。
            int id = innerQueueArrayList.get(elevator.floor).peek().clientId;

            // 时间、总人数更新。
            Elevator.clientNum--;
            elevator.time += 25 * t;

            // 输出乘客进入电梯信息、时间、总人数。
            if(Elevator.clientNum >= 10) System.out.println("乘客" + id + " (目标层：" + (innerQueueArrayList.get(elevator.floor).peek().outFloor + 1)
                    + ") " + "离开了电梯\t\t\t电梯人数：" + Elevator.clientNum + "人" + "\t\t现在时间：" + sdf.format(elevator.time));
            else System.out.println("乘客" + id + " (目标层：" + (innerQueueArrayList.get(elevator.floor).peek().outFloor + 1)
                    + ") " + "离开了电梯\t\t\t电梯人数：" + Elevator.clientNum + "人" + "\t\t\t现在时间：" + sdf.format(elevator.time));
            sleep(25 * t);

            // 执行上一步后，将该层的等待数列中的乘客删除。
            innerQueueArrayList.get(elevator.floor).pop();
        }
        if (Elevator.clientNum < 13) elevator.isFull = false;
    }

    /**
     * 判断是否l层到r-1层都没人进出
     *
     * @param l                   楼层下边界
     * @param r                   楼层上边界
     * @param waitQueueArrayList  每层楼的等待队列
     * @param innerQueueArrayList 每层楼的等待离开电梯队列
     * @return true or false
     */
    private static boolean isAllEmpty(int l, int r, List<LinkedList<Client>> waitQueueArrayList, List<Stack<Client>> innerQueueArrayList) {
        for (int i = l + 1; i < r; i++) if (!waitQueueArrayList.get(i).isEmpty()) return false;

        for (int i = l + 1; i < r; i++) if (!innerQueueArrayList.get(i).isEmpty()) return false;

        return true;
    }
}
