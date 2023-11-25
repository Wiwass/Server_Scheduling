import java.util.ArrayDeque;
import java.util.Queue;

public class Server {

    Queue<Integer> QueueList;
    boolean status;
    boolean queueStatus;
    int ActualProcess;


    public Server(){
        QueueList = new ArrayDeque<Integer>();
        status=true;
        queueStatus=true;
    }
    public boolean getStatus(){
        return status;
    }

    public void AddToQueue(int category){
        QueueList.add(category);
        queueStatus=false;
        load();
    }
    public boolean queueStatus(){
        return queueStatus;
    }
    public void load(){
        if(status && !queueStatus){
            ActualProcess=QueueList.poll();
        }
        if(QueueList.isEmpty()){
            queueStatus=true;
        }
        status=false;
    }
    public int unload(){
        int temp=ActualProcess;
        status=true;
        return temp;
    }
    

}