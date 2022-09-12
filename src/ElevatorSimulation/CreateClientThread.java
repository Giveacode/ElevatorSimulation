package ElevatorSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CreateClientThread extends Thread {
    private List<LinkedList<Client>> waitQueueArrayList = new ArrayList<>(9);

    public CreateClientThread() {

    }

    public CreateClientThread(List<LinkedList<Client>> waitQueueArrayList) {
        this.waitQueueArrayList = waitQueueArrayList;
    }

    @Override
    public void run() {
        while (true) {
            Random random = new Random();
            int delay = (random.nextInt(6) + 5) * 1000;
            try {
                sleep(delay);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Client client;
            client = Tools.randomClient(Main.id);
            waitQueueArrayList.get(client.inFloor).offer(client);
        }
    }
}
