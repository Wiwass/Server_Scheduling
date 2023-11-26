import java.util.ArrayDeque;
import java.util.Queue;

public class Server {

    Queue<Integer> QueueList;
    boolean status;
    boolean queueStatus;
    int ActualProcess;

    /*status: true=libero
     *queue status: true=coda vuota
     */


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
    }
    public boolean queueStatus(){
        return queueStatus;
    }
    public int load(){
        if(status && !queueStatus){
            ActualProcess=QueueList.poll();
            status=false;
        }
        if(QueueList.isEmpty()){
            queueStatus=true;
        }
        
        return ActualProcess;
    }
    public int unload(){
        int temp=ActualProcess;
        status=true;
        return temp;
    }
    

}