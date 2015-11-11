
import java.util.Comparator;

/**
 * Used for sorting lists of Blocks by address.
 * @author Josh Wein
 */
public class BlockComp implements Comparator<Block>{

    @Override
    public int compare(Block b1, Block b2) {
        if(Integer.decode(b1.getAddress()) < Integer.decode(b2.getAddress()))
            return -1;
        else 
            return 1;
    }
    
}
