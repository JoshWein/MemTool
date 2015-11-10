
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
public class Block {
    private int size, end, start, internFrag;
    private boolean allocated;
    private String address;
    private Label label;
    Block(int size, boolean allocated, String address, int label, int internFrag, int memSize) {
        this.size = size;
        this.allocated = allocated;
        this.address = address;
        this.label = new Label(Integer.toString(label));
        this.end = Integer.decode(address) + (size * memSize);
        this.internFrag = internFrag;
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

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the internFrag
     */
    public int getInternFrag() {
        return internFrag;
    }

    /**
     * @param internFrag the internFrag to set
     */
    public void setInternFrag(int internFrag) {
        this.internFrag = internFrag;
    }
}
