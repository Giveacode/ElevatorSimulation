package ElevatorSimulation;

import java.util.Random;
import java.util.stream.IntStream;

import static ElevatorSimulation.Main.t;

public class Tools {
    public static Client randomClient(int id){
        Client client = new Client();
        Random random = new Random();
        while (true) {
            client.inFloor = random.nextInt(9); // 随机生成乘客所在楼层
            client.outFloor = random.nextInt(9); // 随机生成乘客目标楼层
            if(client.inFloor != client.outFloor) break;
        }
        client.waitTime = (random.nextInt(9) + 1)*1000*t; //随机生成乘客的最大等待时间
        client.clientId = id; // 给与乘客id
        Main.id++;
        return client;
    }
}
