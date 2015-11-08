
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Josh
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
