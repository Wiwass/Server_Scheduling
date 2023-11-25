import java.util.ArrayDeque;
import java.util.Queue;

public class Server {
    public Server(){
        Queue<Integer> QueueList= new ArrayDeque<Integer>();
        status=true;
    }
    public boolean getStatus(){
        return status;
    }

    public void AddToQueue(int category){
        QueueList.add(category);
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
        ActualProcess=-1;
        status=true;
        return temp;
    }
    
    Queue<Integer> QueueList;
    boolean status;
    boolean queueStatus;
    int ActualProcess;
}
