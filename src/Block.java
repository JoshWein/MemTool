
import javafx.scene.control.Label;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author josh
 */
class Block {
    private int size;
    private boolean allocated;
    private String address;
    private Label label;
    public int startI;
    public int startJ;
    Block(int size, boolean allocated, String address, int label) {
        this.size = size;
        this.allocated = allocated;
        this.address = address;
        this.label = new Label(Integer.toString(label));
        this.label.setPickOnBounds(true);
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the allocated
     */
    public boolean isAllocated() {
        return allocated;
    }

    /**
     * @param allocated the allocated to set
     */
    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }
    
    public Label getLabel() {
        return label;
    }
    
    public void setLabel(Label label) {
        this.label = label;
    }
}
