import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

class Pair implements Comparable<Pair>{
    public Pair (double insert_key, Object insert_value){
        key=insert_key;
        value=insert_value;
    } 
    public double getKey() {
        return key;
    }
    public Object getValue(){
        return value;
    }
    public void keySwap(double newKey){
        key=(double)newKey;        
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
    public double key;
    public Object value;
}
class Server {

    Queue<Pair> QueueList;
    boolean status;
    boolean queueStatus;
    Pair ActualProcess;
    Pair pointer;

    /*status: true=libero
     *queue status: true=coda vuota
     */


    public Server(){
        QueueList = new ArrayDeque<Pair>();
        status=true;
        queueStatus=true;
        ActualProcess=new Pair(0,0);
    }
    public boolean getStatus(){
        return status;
    }

    public void AddToQueue(double entry_time,int category){
        Pair queue_element = new Pair(entry_time,category);
        QueueList.add(queue_element);
        queueStatus();
    }
    public boolean queueStatus(){
        if(QueueList.isEmpty()){
            queueStatus=true;
            return true;
        }
        queueStatus=false;
        return false;
    }
    public Pair load(){
        if(status && !queueStatus){
            ActualProcess=QueueList.poll();
            status=false;
        }
        queueStatus();
        
        return ActualProcess;
    }
    public Pair unload(){
        Pair temp=ActualProcess;
        status=true;
        return temp;
    }
    public void link_to_loadBalancer(Pair pair){
        pointer=pair;
    }
    public Pair load_status(){
        return pointer;
    }
    

}

/*Simulator variables index:
 * 
 * timestamp=[currenttime,[category,operation,server di riferimento,t_attesa o t_servizio]]
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
    static PriorityQueue<Pair> loadBalancerUpdater(PriorityQueue<Pair> loadBalancer,double lamda_category,int operation,int server,Server[] Servers){
        Pair temp;
        double newKey;
        switch (operation) {
            case 0:
                temp = (Pair)loadBalancer.peek();
                newKey=temp.getKey()+(1/lamda_category);
                loadBalancer.remove(temp);
                temp.keySwap(newKey);
                loadBalancer.add(temp);
                
                break;
        
            case 1:
                temp = (Servers[server]).load_status();
                newKey = temp.getKey()-(1/lamda_category);
                loadBalancer.remove(temp);
                temp.keySwap(newKey);
                loadBalancer.add(temp);
                    
                break;
            
        }
        return loadBalancer;
    }
    static int loadBalancer_index(PriorityQueue<Pair> loadBalancer){
        return (int)loadBalancer.peek().getValue();
    }
    static double Rnd_generator(double lambda, Random random){
        
        double r=random.nextFloat();

        return ((-1)*(1/lambda)*Math.log(1-r));

    }
    static Pair timeStampGenerator(int category,int operation,Random random,double lambda,double currenttime,int server){
        double[]event = new double[4];
        event[0]=operation;
        event[1]=category;
        event[2]=server;
        double delta_time=Rnd_generator(lambda, random);
        event[3]=currenttime;
        Pair timestamp = new Pair(currenttime+delta_time, event);
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
        double[] output = new double[5];
        try{

            FileReader reader = new FileReader(path);
            Scanner scanner = new Scanner(reader);

            String line=scanner.nextLine();
            output=line_extractor(line, 5);
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
    public static void main(String[] args) {



    /*matrici e code necessarie per l'esecuzione del programma */
        String path=args[0];
        double[] parameters=parameters_extraction(path);
        double[][] lambda_collection=lambda_matrix_extraction(path, parameters[1]);
        PriorityQueue<Pair> loadBalancer= new PriorityQueue<>();
    /*parametri passati dal file testuale ed elementi utili per il filling*/ 
        int Server_Number=(int)parameters[0];
        int Numer_Category=(int)parameters[1];
        int Max_numeber_Jobs=(int)parameters[2];
        int repetitions=(int)parameters[3];
        int scheduling_policy=(int)parameters[4];
        Random[][] list_of_Rnd = new Random[Numer_Category][2];
        
        Server[] Servers = new Server[Server_Number];

        
        double AQT=0;
        double ETQA=0;
        double[][] stat_output = new double[Numer_Category][3];
        PriorityQueue<Pair> timeline = new PriorityQueue<>();

        for(int i=0;i<parameters.length;i++){   /*creazione dei primi jobs (uno per parametro) */
            if(i!=0){System.out.print(",");}
            System.out.print((int)parameters[i]);
        }
        System.out.println();
        for(int i=0;i<Numer_Category;i++){   /*creazione dei seed random per le rispettive categorie */
            list_of_Rnd[i][0] = new Random((long)lambda_collection[i][2]);
            list_of_Rnd[i][1] = new Random((long)lambda_collection[i][3]);
        }

    int counter=0;

    while(counter<repetitions){

        counter++;

        /*parametri della simulazione */ 
        int serverIndex=-1;
        int server_pointer=-1;
        int category;
        int operation;
        double currenttime=0;
        int arrived_events=0;
        double delta_time=0;
        Pair temp;
        double[][] server_last_queue_time= new double[Server_Number][2];
        Pair in = new Pair(0, 0);
        double[][] stat_matrix = new double[Numer_Category][2];
        int[] JobCategoryCounter = new int[Numer_Category];
        loadBalancer.clear();
        timeline.clear();
        for(int i=0;i<Server_Number;i++){    /*istanziazione dei server e del loadBalancer*/
            Server newServer = new Server();
            temp = new Pair(0, i);
            newServer.link_to_loadBalancer(temp);
            loadBalancer.add(temp);
            Servers[i]=newServer;
        }
        for(int i=0;i<Numer_Category;i++){
            timeline.add(timeStampGenerator(i, 0, list_of_Rnd[i][0], lambda_collection[i][0], currenttime,-1));
            JobCategoryCounter[i]++;
            
        }        

        while(!timeline.isEmpty()){

            Pair timestamp = timeline.poll();

            double time=(double)timestamp.getKey();
            double[] event=(double[])timestamp.getValue();
            operation=(int)event[0];
            category=(int)event[1];
            server_pointer=(int)event[2];
            delta_time=time-event[3];
            
            if(arrived_events!=Max_numeber_Jobs || operation==1)
                currenttime=time;

            if(arrived_events==Max_numeber_Jobs && operation==0)
                operation=2;
            
            
            switch (operation) {
                case 0:
                    /*load del server */
                    if(scheduling_policy==0){
                        serverIndex=RRscheduling(serverIndex,Server_Number);
                    }
                    else if(scheduling_policy==1){
                        serverIndex=loadBalancer_index(loadBalancer);
                    }
                    if(Servers[serverIndex].getStatus()){
                        Servers[serverIndex].AddToQueue(currenttime,category);
                        arrived_events++;
                        JobCategoryCounter[category]++;
                        Servers[serverIndex].load();
                        if(scheduling_policy==1){
                            loadBalancerUpdater(loadBalancer, lambda_collection[category][1], 0,-1,Servers);
                        }    
                        timeline.add(timeStampGenerator(category, 0, list_of_Rnd[category][0], lambda_collection[category][0], currenttime,-1));
                        timeline.add(timeStampGenerator(category, 1, list_of_Rnd[category][1], lambda_collection[category][1], currenttime,serverIndex));
                        
                    }
                    else{
                        Servers[serverIndex].AddToQueue(currenttime,category);
                        arrived_events++;
                        JobCategoryCounter[category]++;
                        if(scheduling_policy==1){
                            loadBalancerUpdater(loadBalancer, lambda_collection[category][1], 0, -1, Servers);
                        }
                        timeline.add(timeStampGenerator(category, 0, list_of_Rnd[category][0], lambda_collection[category][0], currenttime,-1));
                            
                        
                    }
                    if(repetitions==1 && Max_numeber_Jobs<=10 && scheduling_policy==0){
                        System.out.println(time+",0.0,"+category);
                    }
                    break;

                case 1:
                    /*unload del server */
                    Pair serverOutput=Servers[server_pointer].unload();
                    if(scheduling_policy==1){
                        loadBalancerUpdater(loadBalancer, lambda_collection[(int)serverOutput.getValue()][1], 1,server_pointer,Servers);
                    }
                    
                    if(!Servers[server_pointer].queueStatus()){
                        in = Servers[server_pointer].load();
                        int category_in = (int)in.getValue();
                        timeline.add(timeStampGenerator(category_in, 1, list_of_Rnd[category_in][1], lambda_collection[category_in][1], currenttime,server_pointer));
                        server_last_queue_time[server_pointer][1]=currenttime-in.getKey();
                    }else{
                        server_last_queue_time[server_pointer][1]=0;
                    }
                    
                    if(repetitions==1 && Max_numeber_Jobs<=10 && scheduling_policy==0){
                        System.out.println(time+","+delta_time+","+category);
                    }
                    stat_matrix[category][0]+= server_last_queue_time[server_pointer][0];
                    stat_matrix[category][1]+= delta_time;
                    server_last_queue_time[server_pointer][0]=server_last_queue_time[server_pointer][1];
                    
                    break;
                case 2:
                    JobCategoryCounter[category]--;
                    break;
            }

        }
        ETQA=ETQA+currenttime;
        double Local_AQT=0;
        for(int i=0;i<Numer_Category;i++){
            Local_AQT=Local_AQT+stat_matrix[i][0];
        }
        Local_AQT=Local_AQT/Max_numeber_Jobs;
        AQT+=Local_AQT;
        for(int i=0;i<Numer_Category;i++){
            stat_output[i][0]+=JobCategoryCounter[i];
            stat_output[i][1]+=stat_matrix[i][0]/JobCategoryCounter[i];
            stat_output[i][2]+=stat_matrix[i][1]/JobCategoryCounter[i];
            

        }
        
    }
        System.out.println(ETQA/repetitions);
        System.out.println(AQT/repetitions);
        for(int i=0;i<Numer_Category;i++){
            System.out.println(stat_output[i][0]/repetitions+","+stat_output[i][1]/repetitions+","+stat_output[i][2]/repetitions);
        }
        }
        
    }
