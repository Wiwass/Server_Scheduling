import java.util.Random;
public class Rnd {
    public Rnd(long input_seed){
        seed=input_seed;
        random = new Random();
        random.setSeed(seed);
    }
    double Rnd_generator(double lambda){
        
        double r=random.nextDouble();
        
        return ((-1)*(1/lambda)*Math.log(1-r));

    }
    long getSeed(){
        return seed;
    }
    long seed;
    Random random;
    
}
