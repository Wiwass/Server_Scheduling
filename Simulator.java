import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.ArrayDeque;
import java.util.Queue;

class Pair implements Comparable<Pair>{
    public Pair (float insert_key, Object insert_value){
        key=insert_key;
        value=insert_value;
    } 
    public float getKey() {
        return key;
    }
    public Object getValue(){
        return value;
    }
    @Override public int compareTo(Pair A){
        if(key==A.getKey()){
            return 0;
        }
        if(key>A.getKey()){
            return 1;
        }
        return -1;
    }

    public float key;
    public Object value;
}
class Server {

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

/*
 * Simulator variables index:
 * 
 * timestamp=[currenttime,[category,operation,server di riferimento]]
 * 
 * collections_of_lambdas=[[(lambda arrivo),(lambda servizio),(seed arrivo),(seed servizio)],
 *                        [(lambda arrivo),(lambda servizio),(seed arrivo),(seed servizio)],
 *                          ...
 *                        [(lambda arrivo),(lambda servizio),(seed arrivo),(seed servizio)]]
 * (indice cateory)
 * 
 * collection_of_Rnd=[[rnd_arrivo,rnd_servizio],
 *                    [rnd_arrivo,rnd_servizio],
 *                     ...
 *                    [rnd_arrivo,rnd_servizio]]
 * (indice cateory)
 */


public class Simulator {

    static int RRscheduling(int index,int Server_Number){
        index++;
        if(index==Server_Number){
            index=0;
        }

        return index;
    }
    static float Rnd_generator(double lambda, Random random){
        
        float r=random.nextFloat();
        
        return (float)((-1)*(1/lambda)*Math.log(1-r));

    }
    static Pair timeStampGenerator(int category,int operation,Random random,double lambda,float currenttime,int server){
        int[]event = new int[3];
        event[0]=operation;
        event[1]=category;
        event[2]=server;
        Pair timestamp = new Pair(currenttime+Rnd_generator(lambda, random), event);
        return timestamp;
    }
    static double[] line_extractor(String line, int array_length){
        double[] output= new double[array_length];
        String temp;
        int string_pointer=0;
        double parser;
        int cycle_counter=0;

        try{
        for(int i=0;i<=line.length();i++){
            if(line.charAt(i)==','){
                temp=line.substring(string_pointer, i);
                parser=Double.parseDouble(temp);
                output[cycle_counter]=parser;
                cycle_counter++;
                string_pointer=i+1;
            }
        }}
        catch (Exception e){
            temp=line.substring(string_pointer, line.length());
            parser=Double.parseDouble(temp);
            output[cycle_counter]=parser;
            cycle_counter++;      
        }
        return output;

    }
    static double[] parameters_extraction(String path){
        double[] output = new double[4];
        try{

            FileReader reader = new FileReader(path);
            Scanner scanner = new Scanner(reader);

            String line=scanner.nextLine();
            output=line_extractor(line, 4);
            scanner.close();

        }
        catch(FileNotFoundException e){}
        
        return output;

    }
    static double[][] lambda_matrix_extraction(String path,double parameters){
        int length=(int)parameters;
        double output[][]= new double[length][4];
        int line_counter=0;

        try{

            FileReader reader = new FileReader(path);
            Scanner scanner = new Scanner(reader);
            String line=scanner.nextLine();

            while(line_counter<parameters){

                line=scanner.nextLine();
                output[line_counter]=line_extractor(line, 4);
                line_counter++;

            }
        
            scanner.close();
        }

        catch(FileNotFoundException e){}
        return output;

    }
    static int condition(int[] array){
        int sum=0;
        for(int i=0;i<array.length;i++){
            sum=sum+array[i];
        }
        return sum-array.length;

    }
    public static void main(String[] args) {

    
        double[] parameters=parameters_extraction("parameters.txt");
        double[][] lambda_collection=lambda_matrix_extraction("parameters.txt", parameters[1]);


        System.out.print("|");
        for(int i=0;i<parameters.length;i++){
           System.out.print(parameters[i]+"|");
        }
        System.out.println();


        int Server_Number=(int)parameters[0];
        int Numer_Category=(int)parameters[1];
        int Max_numeber_Jobs=(int)parameters[2];
        int repetitions=(int)parameters[3];
        Random[][] list_of_Rnd = new Random[Numer_Category][2];


        int[] JobCategoryCounter = new int[Numer_Category];
        Server[] Servers = new Server[Server_Number];

        Arrays.fill(JobCategoryCounter, 1);

        for(int i=0;i<Server_Number;i++){
            Server newServer = new Server();
            Servers[i]=newServer;
        }
        for(int i=0;i<Numer_Category;i++){
            list_of_Rnd[i][0] = new Random((long)lambda_collection[i][2]);
            list_of_Rnd[i][1] = new Random((long)lambda_collection[i][3]);
        }

        float currenttime=0;
        int serverIndex=0;
        int server_pointer=-1;
        int category;
        int operation;
        int[] event;
        int counter=0;

        System.out.println("----------------------");

    while(counter<=repetitions){

        counter++;

        Queue<Pair> timeline = new PriorityQueue();

        for(int i=0;i<Numer_Category;i++){
            timeline.add(timeStampGenerator(i, 0, list_of_Rnd[i][0], lambda_collection[i][0], currenttime,-1));
        }

        while(condition(JobCategoryCounter)<Max_numeber_Jobs){
            Pair timestamp = timeline.poll();
            float time=(float)timestamp.getKey();

            event=(int[])timestamp.getValue();
            operation=event[0];
            category=event[1];
            server_pointer=event[2];
            
            currenttime=time;
            switch (operation) {
                case 0:
                    /*load del server */
                    serverIndex=RRscheduling(serverIndex,Server_Number);
                    if(Servers[serverIndex].getStatus()){
                        Servers[serverIndex].AddToQueue(category);
                        Servers[serverIndex].load();
                        timeline.add(timeStampGenerator(category, 0, list_of_Rnd[category][0], lambda_collection[category][0], currenttime,-1));
                        timeline.add(timeStampGenerator(category, 1, list_of_Rnd[category][1], lambda_collection[category][1], currenttime,serverIndex));
                    }
                    Servers[serverIndex].AddToQueue(category);
                    timeline.add(timeStampGenerator(category, 0, list_of_Rnd[category][0], lambda_collection[category][0], currenttime,-1));

                    break;

                case 1:
                    /*unload del server */
                    int serverOutput=Servers[server_pointer].unload();
                    JobCategoryCounter[serverOutput]=JobCategoryCounter[serverOutput]+1;
                    category=Servers[server_pointer].load();
                    timeline.add(timeStampGenerator(category, 1, list_of_Rnd[category][1], lambda_collection[category][1], currenttime,server_pointer));
                    /*calcolo del tempo di esecuzione nel server */
                    break;
            }


        }
        System.out.println("ETA:"+currenttime);
        
        System.out.println("----------------------");


    }

        }   
    }