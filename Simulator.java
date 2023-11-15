import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;



/*
 * Simulator
 */
public class Simulator {
    static float[] extraction(String path){
        float[] output = new float[8];
        try{

        FileReader reader = new FileReader(path);
        Scanner scanner = new Scanner(reader);
        scanner.useDelimiter(",");
        String line;
        float parser;
        int cycle_counter=0;
    
    try{
    while (scanner.hasNext()) {
        line=scanner.next();
        parser=Float.parseFloat(line);
        output[cycle_counter]=parser;
        cycle_counter++;
    }
    }
    catch (Exception e) {
        
    }        
        scanner.close();

        }catch(FileNotFoundException e){}
        return output;

    }
    public static void main(String[] args) {
        float[] parameters=extraction("parameters.txt");
        for(int i=0;i<parameters.length;i++){
           System.out.println(parameters[i]);
        }

        
    }

    
}