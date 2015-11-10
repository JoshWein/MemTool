/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 *
 * @author Josh Wein
 */
public class FXMLDocumentController implements Initializable {
    @FXML
    Parent root;
    @FXML
    private Label listTypeLabel, memStartLabel, memEndLabel, heapSizeLabel, memRowLabel;
    @FXML
    private Pane table;
    @FXML
    private PieChart pie, pie1;
    Stage stage;
    // Data for parsing
    public static final String SPACEDELIM = "[ ]+";
    public int memSize, heapSize, heapStart, heapEnd, totalSize, grid, allocatedSize, freeSize, requested, recieved;
    public static double blockWidth, blockHeight;  
    public boolean full, error;
    Rectangle [] rec;
    Block [][] links;
    Rectangle recty;
    Popup popup;
    Label label;
    // Stores the indexes of the first block in ever group
    public static int[] groupHeads;

    /**
     *
     */
    public LinkedList<Block> blockList;
    DropShadow ds = new DropShadow();
    Reflection reflection = new Reflection();
    
    @FXML
    private void loadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        if(file == null)
            return;
        resetLabels();
        load(file);
    }
    
    private void load(File file) {
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            parseFile(content);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    *   Main file parsing function. Adds given free block to the list first and then sorts and adds appropriate allocated blocks. 
    *   @param content - the file read in
    *   Splits string on newlines and does error checking on each line.
    */
    private void parseFile(String content) {
        error = false;
        String delims = "\n";
        String[] tokens = content.split(delims);
        parseFirstLine(tokens[0]);
        // Print out given time
        if(error)
            return;
        blockList = new LinkedList<>();
        // Go through each line one by and and display memory information
        int size;
        totalSize = 0; freeSize = 0; allocatedSize = 0; requested = 0; recieved = 0;
        for(int i = 2; i < tokens.length && !error; i++) {
            String[] lineTokens = tokens[i].split(SPACEDELIM);
            if(lineTokens.length != 2 && lineTokens.length != 3) {
                error("Line " + i + ": There was an issue parsing this line. Check and make sure there are the correct number of arguments");
            } else {
                // Get starting address to calculate ending address 
                size = Integer.parseInt(lineTokens[1].trim());
                size /= memSize;
                totalSize += size;
                if(lineTokens.length == 3) { // This means it's full memory information.
                    full = true; 
                    requested += Integer.parseInt(lineTokens[2].trim());
                    recieved += size*memSize;
                    int internFrag = Integer.parseInt(lineTokens[2].trim());
                    allocatedSize += size;
                    addBlock(size, true, lineTokens[0], blockList.size()+1, (size*memSize) - internFrag, -1);
                } else {
                    addBlock(size, false, lineTokens[0], blockList.size()+1, 0, -1);  
                }
            }
        }
        if(!error) {
            System.out.println("Heap End: 0x" + Integer.toHexString(heapEnd));
            // Sort free blocks
            sortFree(blockList);
            // Insert allocated blocks
            if(!full)
                fixList(blockList);
            printBlocks();
            initTable();
            generateSquares();
            colorSquares();
            createPie();
            createRectandPop();
        }
    }
    
    /*
    *   Method for parsing and checking the first line of a snapshot file.
    *   @param  line - line to be parsed
    */
    private void parseFirstLine(String line) {
        String[] tokens = line.split(SPACEDELIM);
        if(tokens.length != 3) {
            error = true;
            error("First line does not have 3 arguments. Should be: 'List Type', 'Memory Row Size', 'Total Heap Size'");
            return;
        }
        // Parse and print first line
        if(tokens[0].toUpperCase().equals("EXPLICIT")) {            
            this.listTypeLabel.setText("Explicit");
        } else if (tokens[0].toUpperCase().equals("IMPLICIT")) {
            this.listTypeLabel.setText("Implicit");
        } else {
            error("Line 1: Visualizer only supports explicit free lists. The given freelist type does not match.");
            return;
        }
        memSize = Integer.parseInt(tokens[1]);
        this.memRowLabel.setText(Integer.toString(memSize));
        heapSize = Integer.parseInt(tokens[2].trim());
        //this.heapSizeLabel.setText(Integer.toString(heapSize));
        System.out.printf("%-35.35s %-35.35s %s\n", tokens[0].trim(), "Memory Row Size: " + memSize + " bytes", "Total Heap Size: " + heapSize + " bytes");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    


    void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // For errors while parsing the main portion of the file
    private void error(String error){
        this.error = true;
        Alert alert = new Alert(AlertType.ERROR, error);
        alert.showAndWait();
        resetLabels();
    }
    
    private void resetLabels() {
        listTypeLabel.setText("");
        memStartLabel.setText("");
        memEndLabel.setText("");
        heapSizeLabel.setText("");
        memRowLabel.setText("");
        table.getChildren().clear();
    }
    
    // Calculate appropriate sized grid for displaying all blocks.
    private void initTable() {
        for(grid = 0; grid * grid < totalSize; grid++);
        //System.out.println("Need " + grid + " X " + grid + " size grid");
        //System.out.println("Pane Width: " + this.table.getWidth() + " Pane Height: " + this.table.getHeight());
        blockWidth = this.table.getWidth()/(double)grid;
        blockHeight = this.table.getHeight()/(double)grid;
        // Ensure blocks are always squares and always can fit in the pane
        if(blockWidth > blockHeight)
            blockWidth = blockHeight;
        //System.out.println("Each block should be " + blockWidth + " by " + blockWidth);
    }

    // Calculate appropriate sized grid for displaying all blocks.
    private void calculateTable() {
        blockWidth = this.table.getWidth()/(double)grid;
        blockHeight = this.table.getHeight()/(double)grid;
        // Ensure blocks are always squares and always can fit in the pane
        if(blockWidth > blockHeight)
            blockWidth = blockHeight;
    }
    private void generateSquares() {
        rec = new Rectangle [grid*grid];
        System.out.println(grid);
        for(int i = 0, row = 0, col = 0; i < grid*grid; i++) {
            rec[i] = new Rectangle();
            rec[i].setX(col%grid * blockWidth);
            rec[i].setY((row%grid) * blockWidth);
            rec[i].setWidth(blockWidth);
            rec[i].setHeight(blockWidth);
            rec[i].setFill(null);
            rec[i].setStroke(Color.BLACK);
            //ds.setInput(reflection);                
            if(grid <= 40)
                rec[i].setEffect(ds);
            table.getChildren().add(rec[i]);
            col++;
            if(col == grid) {
                col = 0;
                row++;
            }      
        }
    }
    
    // Highlights whole block that is being hovered over
    void highlightBlock(int i) {
        int j = groupHeads.length-1;
        // Find start of block
        while(groupHeads[j] > i)
            j--;
        int k = groupHeads[j], head = groupHeads[j];
        if(j+1 < groupHeads.length)
            j = groupHeads[j+1];
        else
            j = totalSize;
        while(k != j)
            rec[k++].setStroke(Color.WHITE);
        // Find prev and next blocks
        Block prev = null, next = null;
        for(int iter = 0; iter < links.length; iter++) {
            if(links[iter][0].getStart() == head) { // Found link
                if(iter != 0)
                    prev = links[iter][1];
                if(iter != links.length - 1)
                    next = links[iter][2];
                break;
            }
        }
        // Highlight previous
        if(prev != null) {
            k = prev.getStart();
            while(k != prev.getEnd())
                rec[k++].setStroke(Color.ORANGE);
        }
        // Highlight next
        if(next != null) {
            k = next.getStart();
            while(k != next.getEnd())
                rec[k++].setStroke(Color.BLUE);
        }
    }
    
    void unhighlightBlock(int i) {
        int j = groupHeads.length-1;
        // Find start of block
        while(groupHeads[j] > i)
            j--;
        int k = groupHeads[j], head = groupHeads[j];
        if(j+1 < groupHeads.length)
            j = groupHeads[j+1];
        else
            j = totalSize;
        while(k != j)
            rec[k++].setStroke(Color.BLACK);
        // Find prev and next blocks
        Block prev = null, next = null;
        for(int iter = 0; iter < links.length; iter++) {
            if(links[iter][0].getStart() == head) { // Found link
                if(iter != 0)
                    prev = links[iter][1];
                if(iter != links.length - 1)
                    next = links[iter][2];
                break;
            }
        }
        // UnHighlight previous
        if(prev != null) {
            k = prev.getStart();
            while(k != prev.getEnd())
                rec[k++].setStroke(Color.BLACK);
        }
        // UnHighlight next
        if(next != null) {
            k = next.getStart();
            while(k != next.getEnd())
                rec[k++].setStroke(Color.BLACK);
        }
    }
    
    private void resizeSquares() {
        //clearLabels();
        int size = grid * grid;
        double x = 0, y = 0;
        for(int i = 0, row = 0, col = 0; i < size; i++) {
            rec[i].setX(x);
            rec[i].setY(y);
            rec[i].setWidth(blockWidth);
            rec[i].setHeight(blockWidth);    
            col++;            
            if(col == grid) {
                col = 0;
                x = 0;
                row++;
                y = row * blockWidth;
            } else {
                x = col * blockWidth;
            }      
        }
    }
    
    private void colorSquares(){
        Block block;
        int i = 0, j = 0, k = 0;
        int currentAddr = heapStart;
        //String currentAddrs;
        groupHeads = new int[blockList.size()];
        while(k < blockList.size()){
            groupHeads[k] = i;
            blockList.get(k).setStart(i);
            //System.out.println(i);
            block = blockList.get(k);
            int size = block.getSize();
            block.setEnd(block.getStart() + (size));
            int tagSize = size * memSize;
            block.getLabel().setLayoutX(rec[i].getX() + 1);
            block.getLabel().setLayoutY(rec[i].getY());
            //table.getChildren().add(block.getLabel());
            //System.out.println(size);
            while(size != 0) {                 
                //currentAddrs = "Block: " + (k+1) + " Address: 0x" + Integer.toHexString(currentAddr) + " Size: " + tagSize;
                initEventHandler(i, "Block: " + (k+1) + " Address: 0x" + Integer.toHexString(currentAddr) + " Size: " + tagSize);
                currentAddr += memSize;
                if(block.isAllocated()) {
                    rec[i++].setFill(Color.web("#800000"));
                    //Tooltip.install(rec[i++], new Tooltip(currentAddrs));
                }
                else {
                    rec[i++].setFill(Color.GREEN);
                    //Tooltip.install(rec[i++], new Tooltip(currentAddrs));
                }
                size--;
            }
            k++;
        }
    }
    
    private void createRectandPop(){
        recty = new Rectangle(220, 30, Color.web("#0C0C0C"));
        recty.setArcHeight(10);
        recty.setArcWidth(10);
        recty.setOpacity(.8);
        popup = new Popup();
        label = new Label();
        label.setTextFill(Color.web("#E6E6FF"));
        label.setLayoutX(label.getLayoutX()+10);
        label.setLayoutY(label.getLayoutY()+5);
        label.setFont(Font.font("System Regular", 12));
        popup.getContent().addAll(recty, label); 
    }
    private void initEventHandler(int i, String info) {
        rec[i].setOnMouseEntered((MouseEvent t) -> {
            label.setText(info);
            popup.setX(t.getScreenX() + 10);
            popup.setY(t.getScreenY() + 5);
            popup.show(stage);
            recty.setWidth(label.getWidth() + 20);
            recty.setHeight(label.getHeight() + 10);
            highlightBlock(i);
        });
        rec[i].setOnMouseExited((MouseEvent t) -> {
            popup.hide();
            unhighlightBlock(i);
        });
    }
    public void refresh() {
        if(rec != null) {
            calculateTable();
            resizeSquares();
        }
    }

    private void createPie() {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Free", (((double)totalSize - (double)allocatedSize)/(double)totalSize* 100)),
                new PieChart.Data("Allocated", ((double)allocatedSize/(double)totalSize)* 100) );
        pie.setData(pieChartData);
        pie.setStartAngle(30);
        pie.getData().get(0).getNode().setStyle("-fx-pie-color: GREEN");
        pie.getData().get(1).getNode().setStyle("-fx-pie-color: #800000");
        pie.setLabelsVisible(false);
        Set<Node> items = pie.lookupAll("Label.chart-legend-item");
        int i = 0;
        Color[] colors = { Color.web("GREEN"), Color.web("#800000")};
        for (Node item : items) {
          Label label = (Label) item;
          final Rectangle rectangle = new Rectangle(10, 10, colors[i]);
          label.setGraphic(rectangle);
          i++;
        }
        for (final PieChart.Data data : pie.getData()) {
            //System.out.println(data.getPieValue());
            Tooltip.install(data.getNode(), new Tooltip(Math.round(data.getPieValue()) + "%"));
            data.getNode().setOnMouseEntered((MouseEvent event) -> {
                data.getNode().setScaleX(1.1);
                data.getNode().setScaleY(1.1);
            });
            data.getNode().setOnMouseExited((MouseEvent event) -> {
                data.getNode().setScaleX(1);
                data.getNode().setScaleY(1);
            });
        }
        if(full) {
            double percent = ((double)requested/(double)recieved) * 100;
            pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Requested", percent),
                new PieChart.Data("Recieved", 100 - percent));
        pie1.setData(pieChartData);
        pie1.setStartAngle(30);
        pie1.getData().get(0).getNode().setStyle("-fx-pie-color: GREEN");
        pie1.getData().get(1).getNode().setStyle("-fx-pie-color: #800000");
        pie1.setLabelsVisible(false);
        items = pie1.lookupAll("Label.chart-legend-item");
        i = 0;
        for (Node item : items) {
          Label label = (Label) item;
          final Rectangle rectangle = new Rectangle(10, 10, colors[i]);
          label.setGraphic(rectangle);
          i++;
        }
        for (final PieChart.Data data : pie1.getData()) {
            //System.out.println(data.getPieValue());
            Tooltip.install(data.getNode(), new Tooltip(Math.round(data.getPieValue()) + "%"));
            data.getNode().setOnMouseEntered((MouseEvent event) -> {
                data.getNode().setScaleX(1.1);
                data.getNode().setScaleY(1.1);
            });
            data.getNode().setOnMouseExited((MouseEvent event) -> {
                data.getNode().setScaleX(1);
                data.getNode().setScaleY(1);
            });
        }
        }
    }
    
    // Prints the list of blocks.
    private void printBlocks() {
        System.out.println("-----Block List-----");
        blockList.stream().forEach((block) -> {
            System.out.println(block.getAddress() + " " + block.getSize() + " " + (block.isAllocated() ? "Allocated" : "Free"));
        });
        System.out.println("--------------------");
    }

    // Creates a new block from the given parameters and add it to the block list.
    private void addBlock(int size, boolean b, String lineToken, int label, int internFrag, int pos) {
        if(pos == -1)
            blockList.add(new Block(size, b, lineToken, label, internFrag, memSize));
        else
            blockList.add(pos, new Block(size, b, lineToken, label, internFrag, memSize));
    }

    // Sorts given list of blocks from lowest memory address to highest
    // Also creates a list of links
    private void sortFree(LinkedList<Block> blockList) {
        links = new Block[blockList.size()][3];
        for(int i = 0; i < blockList.size(); i++) {
            links[i][0] = blockList.get(i);
            if(i != 0)
                links[i][1] = blockList.get(i-1);
            if(i != blockList.size()-1)
                links[i][2] = blockList.get(i+1);
        }
        Collections.sort(blockList, new BlockComp());
    }
    
    /*
    *   Takes given sorted list and identifying information, scans and finds open spots and inserts allocated blocks.
    *   @param blockList: list of blocks to scan, sorted lowest to highest
    *   @param totalSize: current size of list in bytes/memRowSize
    *   @param heapSize: size of heap that needs to be filled
    *   @param memSize: memory row size
    */
    private void fixList(LinkedList<Block> blockList) {
        int size;
        // See if we need to add an allocated block before all free blocks.
        // Check through the list
        for(int i = 0; i < blockList.size() - 1; i++) {
            if(blockList.get(i).getEnd() != Integer.decode(blockList.get(i+1).getAddress())){
                //System.out.println(Integer.decode(blockList.get(i+1).getAddress()) - blockList.get(i).getEnd());
                size = Integer.decode(blockList.get(i+1).getAddress()) - blockList.get(i).getEnd();
                size /= memSize;
                totalSize += size;
                allocatedSize += size;
                addBlock(size, true, "0x" + Integer.toHexString(blockList.get(i).getEnd()), blockList.size()+1, 0, i+1);
            }
            System.out.println(totalSize*memSize + " " + allocatedSize + " " + heapSize);
        }
        // See if we need to add an allocated block at the end
        if(totalSize < heapSize) {
            size = heapSize - (totalSize*memSize);
            //System.out.println("0x" + Integer.toHexString(Integer.decode(blockList.get(blockList.size()-1).getAddress()) + (blockList.get(blockList.size()-1).getSize()*memSize)) + " " + size);
            size /= memSize;
            totalSize += size;
            allocatedSize += size;
            addBlock(size, true, "0x" + Integer.toHexString(Integer.decode(blockList.get(blockList.size()-1).getAddress()) + (blockList.get(blockList.size()-1).getSize()*memSize)), blockList.size()+1, 0, -1);
        }
        // Set labels
        int converted = Integer.decode(blockList.get(0).getAddress());
        heapStart = converted;
        this.memStartLabel.setText("0x" + Integer.toHexString(converted));
        heapSize = totalSize*memSize;
        heapEnd = converted + heapSize;
        this.memEndLabel.setText("0x" + Integer.toHexString(heapEnd));
        this.heapSizeLabel.setText(Integer.toString(heapSize));
    }
}
