import java.util.Random;
public class Rnd {
    double Rnd_generator(long lambda,long seed){
        Random random = new Random();
        random.setSeed(seed);
        double r=random.nextFloat();
        
        return ((-1)*(1/lambda)*Math.log(1-r));

    }
    
}
