import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/*
 * Simulator
 * LambdasA,LambdasB=[lambda di ogni categoria]
 * timestamp=[currenttime,[category,operation]]
 */


public class Simulator {

    static int RRscheduling(int index,int Server_Number){
        index++;
        if(index==Server_Number){
            index=0;
        }

        return index;
    }
    static Pair timeStampGenerator(int category,int operation,Rnd random,double lambda,float currenttime,int server){
        int[]event = new int[3];
        event[0]=category;
        event[1]=operation;
        event[2]=server;
        Pair timestamp = new Pair(currenttime+random.Rnd_generator(lambda), event);
        return timestamp;
    }
    static double[] extraction(String path){
        double[] output = new double[9];
        try{

        FileReader reader = new FileReader(path);
        Scanner scanner = new Scanner(reader);
        String line;
        String temp;
        int string_pointer=0;
        double parser;
        int cycle_counter=0;
        int line_counter=-1;
    
    try{
    
        line=scanner.nextLine();
        /*line=line.replace(".",",");*/
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

        Boolean break_condition=true;
        while(break_condition) {
        line=scanner.nextLine();
        if(line!=""){
            break_condition=false;
        }
         line_counter++;
        }
        output[8]=line_counter;
        string_pointer=0;

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
            
        }
    }
    catch (Exception e) {
        
    }        
        scanner.close();

        }catch(FileNotFoundException e){}
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
        double[] parameters=extraction("parameters.txt");

        for(int i=0;i<parameters.length;i++){
           System.out.println(parameters[i]);
        }



        int Server_Number=(int)parameters[0];
        int Numer_Category=(int)parameters[1];
        int Max_numeber_Jobs=(int)parameters[2];
        int repetitions=(int)parameters[3];
        double lambdaA = parameters[4];
        double lambdaB = parameters[5];
        long seedA = (new Double(parameters[6])).longValue();
        long seedB = (new Double(parameters[7])).longValue();

        Rnd randomA = new Rnd(seedA);
        Rnd randomB = new Rnd(seedB);

        int[] JobCategoryCounter = new int[Numer_Category];
        double[] LambdasA = new double[Numer_Category];
        double[] LambdasB = new double[Numer_Category];
        Server[] Servers = new Server[Server_Number];

        Arrays.fill(JobCategoryCounter, 1);
        Arrays.fill(LambdasA, lambdaA);
        Arrays.fill(LambdasB, lambdaB);
        for(int i=0;i<Server_Number;i++){
            Server newServer = new Server();
            Servers[i]=newServer;
        }

        float currenttime=0;
        int sereverIndex=0;
        int server_pointer=-1;

        Queue<Pair> timeline = new PriorityQueue();

        for(int i=0;i<Numer_Category;i++){
            timeline.add(timeStampGenerator(i, 0, randomA, lambdaA, currenttime,server_pointer));
        }

        while(condition(JobCategoryCounter)<Max_numeber_Jobs){
            Pair timestamp = timeline.poll();
            float time=(float)timestamp.getKey();

            int[] event=(int[])timestamp.getValue();
            int operation=event[0];
            int category=event[1];
            server_pointer=event[2];
            
            currenttime=time;

            switch (operation) {
                case 0:
                    /*load del server */
                    int loadindex=RRscheduling(sereverIndex,Server_Number);
                    Servers[loadindex].AddToQueue(category);
                    break;
            
                case 1:
                    /*unload del server */
                    int serverOutput=Servers[server_pointer].unload();
                    JobCategoryCounter[serverOutput]=JobCategoryCounter[serverOutput]+1;
                    /*calcolo del tempo di esecuzione nel server */
                    break;
            }


        }



System.out.println(currenttime);
        }
        
    }


    
/*Cimitero delle cose:
 * 
 * 
 * 
 * 
double[] Jobs = new double[Max_numeber_Jobs];
        for (int i=0;i<Jobs.length;i++){
            Jobs[i]=randomA.Rnd_generator(lambdaA);
            System.out.println(Jobs[i]);
        }
        double[] waits = new double[Max_numeber_Jobs];
        for (int i=0;i<waits.length;i++){
            waits[i]=randomB.Rnd_generator(lambdaB);
            System.out.println(waits[i]);



public class Heap_Implementation {
    public Heap_Implementation(int initial_entry){
        Heap = new Pair[initial_entry];
        for(int i=0;i<initial_entry;i++){

            Pair entry = new Pair(assigned_priority_key, value);



        }

        


    }

private int entry_number;
private Pair[] Heap;
}


 */