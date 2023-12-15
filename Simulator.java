import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

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
    public void keySwap(double newKey){
        key=(float)newKey;         /*motlo finniky */
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
    Pair pointer;

    /*status: true=libero
     *queue status: true=coda vuota
     */


    public Server(){
        QueueList = new ArrayDeque<Integer>();
        status=true;
        queueStatus=true;
        ActualProcess=-1;
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
    public void link_to_loadBalancer(Pair pair){
        pointer=pair;
    }
    public Pair load_status(){
        return pointer;
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
    static PriorityQueue<Pair> loadBalancerUpdater(PriorityQueue<Pair> loadBalancer,double lamda_category,int operation,int server,Server[] Servers){
        Pair temp;
        double newKey;
        switch (operation) {
            case 0:
                temp = (Pair)loadBalancer.peek();
                newKey=temp.getKey()+(1/lamda_category);
                temp.keySwap(newKey);
                loadBalancer.poll();
                Servers[(int)temp.getValue()].link_to_loadBalancer(temp);
                loadBalancer.add(temp);
                break;
        
            case 1:
                temp = (Servers[server]).load_status();
                loadBalancer.remove(temp);
                newKey = temp.getKey()-(1/lamda_category);
                temp.keySwap(newKey);
                Servers[(int)temp.getValue()].link_to_loadBalancer(temp);
                loadBalancer.add(temp);
            
                
                
                break;
        }
        return loadBalancer;
    }
    static int loadBalancer_index(PriorityQueue<Pair> loadBalancer){
        int output=(int)loadBalancer.peek().getValue();
        return output;
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
    static int condition(int[] array){
        int sum=0;
        for(int i=0;i<array.length;i++){
            sum=sum+array[i];
        }
        return sum;

    }
    static void timeline_print(PriorityQueue<Pair> x){
        Pair print;
        int [] temp;
        Stack<Pair> stack = new Stack<Pair>();
        while(!x.isEmpty()){
            print=x.poll();
            stack.add(print);

            System.out.println(print.getKey());
            temp=(int[])print.getValue();
            System.out.println(" o:"+temp[0]+"/"+"c:"+temp[1]+"/"+"s:"+temp[2]);
            System.out.println();
        }
        while(!stack.isEmpty()){
            x.add(stack.pop());
        }
        System.out.println("---------------");
    }
    public static void main(String[] args) {



    /*matrici e code necessarie per l'esecuzione del programma */
        String path="IO-EXAMPLES/input_K3_H2_N8_R1_P0.in";
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
        int[] JobCategoryCounter = new int[Numer_Category];
        Server[] Servers = new Server[Server_Number];
        Pair temp;

        
    /*filling adeguato degli array prima di iniziare l'esecuzione della simulazione */
        Arrays.fill(JobCategoryCounter, 0);
        System.out.print("|");
        for(int i=0;i<parameters.length;i++){   /*creazione dei primi jobs (uno per parametro) */
           System.out.print((int)parameters[i]+"|");
        }
        System.out.println();
        for(int i=0;i<Server_Number;i++){    /*istanziazione dei server e del loadBalancer*/
            Server newServer = new Server();
            temp = new Pair(0, i);
            newServer.link_to_loadBalancer(temp);
            loadBalancer.add(temp);
            Servers[i]=newServer;
        }
        for(int i=0;i<Numer_Category;i++){   /*creazione dei seed random per le rispettive categorie */
            list_of_Rnd[i][0] = new Random((long)lambda_collection[i][2]);
            list_of_Rnd[i][1] = new Random((long)lambda_collection[i][3]);
        }

    int counter=1;
    

        System.out.println("----------------------");

    while(counter<=repetitions){

        /*parametri della simulazione */ 
        int serverIndex=-1;
        int server_pointer=-1;
        int category;
        int operation;
        float currenttime=0;

        counter++;
 
        PriorityQueue<Pair> timeline = new PriorityQueue<>();

        for(int i=0;i<Numer_Category;i++){
            timeline.add(timeStampGenerator(i, 0, list_of_Rnd[i][0], lambda_collection[i][0], currenttime,-1));
        }
        timeline_print(timeline);

        while(condition(JobCategoryCounter)<Max_numeber_Jobs){
            Pair timestamp = timeline.poll();

            float time=(float)timestamp.getKey();
            int[] event=(int[])timestamp.getValue();
            operation=event[0];
            category=event[1];
            server_pointer=event[2];
            
            currenttime=time;
            
            switch (operation) {
                case 0:
                    /*load del server */
                    if(scheduling_policy==0){
                        serverIndex=RRscheduling(serverIndex,Server_Number);
                    }
                    if(scheduling_policy==1){
                        serverIndex=loadBalancer_index(loadBalancer);
                    }
                    if(Servers[serverIndex].getStatus()){
                        Servers[serverIndex].AddToQueue(category);
                        Servers[serverIndex].load();
                        if(scheduling_policy==1){
                         loadBalancer=loadBalancerUpdater(loadBalancer, lambda_collection[category][1], 0,-1,Servers);
                        }
                        timeline.add(timeStampGenerator(category, 0, list_of_Rnd[category][0], lambda_collection[category][0], currenttime,-1));
                        timeline.add(timeStampGenerator(category, 1, list_of_Rnd[category][1], lambda_collection[category][1], currenttime,serverIndex));
                    }
                    else{
                        Servers[serverIndex].AddToQueue(category);
                        timeline.add(timeStampGenerator(category, 0, list_of_Rnd[category][0], lambda_collection[category][0], currenttime,-1));
                    }
                    break;

                case 1:
                    /*unload del server */
                    int serverOutput=Servers[server_pointer].unload();
                    if(scheduling_policy==1){
                        loadBalancer=loadBalancerUpdater(loadBalancer, lambda_collection[serverOutput][1], 1,server_pointer,Servers);
                    }
                    JobCategoryCounter[serverOutput]=JobCategoryCounter[serverOutput]+1;
                    category=Servers[server_pointer].load();
                    /*timeline.add(timeStampGenerator(serverOutput, 0, list_of_Rnd[serverOutput][0], lambda_collection[serverOutput][0], currenttime, -1)); */
                    timeline.add(timeStampGenerator(category, 1, list_of_Rnd[category][1], lambda_collection[category][1], currenttime,server_pointer));
                    /*calcolo del tempo di esecuzione nel server */
                    break;
            }



            /*struttura di debug */
            System.out.println("time:"+currenttime+","+""+",ct:"+category+",op:"+operation+",serverI:"+serverIndex+",serverP:"+server_pointer);
            System.out.print("|");
            for(int i=0;i<JobCategoryCounter.length;i++){   /*creazione dei primi jobs (uno per parametro) */
                System.out.print(JobCategoryCounter[i]+"|");
            }
            System.out.println();
            System.out.println();
            if(false){
                timeline_print(timeline);
                System.out.println();
            }



        }
        System.out.println("ETA:"+currenttime);
        System.out.print("|");
        for(int i=0;i<JobCategoryCounter.length;i++){   /*creazione dei primi jobs (uno per parametro) */
           System.out.print(JobCategoryCounter[i]+"|");
        }
        
        Arrays.fill(JobCategoryCounter, 1);

        loadBalancer.clear();
        for(int i=0;i<Server_Number;i++){    /*istanziazione dei server e del loadBalancer*/
            Server newServer = new Server();
            temp = new Pair(0, i);
            newServer.link_to_loadBalancer(temp);
            loadBalancer.add(temp);
            Servers[i]=newServer;
        }
        timeline.clear();
        System.out.println();
        System.out.println("----------------------");
        
        




    }

        }   
    }




/*cimitero 2.0
 * 
 * Stack<Pair> stack = new Stack<>();
                Pair stack_element = loadBalancer.poll();
                int current_server = (int)stack_element.getValue();
                int counter=0;

                while(current_server!=server){
                    stack.add(stack_element);
                    stack_element = loadBalancer.poll();
                    current_server = (int)stack_element.getValue();
                    
                    counter++;
                }
                
                newKey=stack_element.getKey()-(1/lamda_category);
                stack_element.keySwap(newKey);

                loadBalancer.add(stack_element);
                
                for(int i=0;i<counter;i++){
                    loadBalancer.add(stack.pop());
                }
            
            
            
        System.out.println(scheduling_policy);
            if(scheduling_policy==0){
            System.out.println("-----------------");
            System.out.print("cambio della scheduling policy"+"\n"+scheduling_policy+"->");
        }
        scheduling_policy=1;
 */