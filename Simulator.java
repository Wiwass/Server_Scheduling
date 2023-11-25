import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;



/*
 * Simulator
 */
public class Simulator {
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


        double[] Jobs = new double[Max_numeber_Jobs];
        for (int i=0;i<Jobs.length;i++){
            Jobs[i]=randomA.Rnd_generator(lambdaA);
            System.out.println(Jobs[i]);
        }
        double[] waits = new double[Max_numeber_Jobs];
        for (int i=0;i<waits.length;i++){
            waits[i]=randomB.Rnd_generator(lambdaB);
            System.out.println(waits[i]);
        }
        
    }


    
}