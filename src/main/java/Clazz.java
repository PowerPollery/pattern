import java.util.HashMap;

/**
 * 类别
 */
public class Clazz extends HashMap<String, Integer> {
    public int count = 0;
    public Clazz(){super();}
    public Clazz(int count){
        super();
        this.count = count;
    }
}
