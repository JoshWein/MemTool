/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
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
    Stage stage;
    // Data for parsing
    public static final String SPACEDELIM = "[ ]+";
    public static int memSize, heapSize, heapStart, heapEnd, totalSize, grid;
    public static double blockWidth, blockHeight;  
    public static boolean error;
    Rectangle [][] rec;

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
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            parseFile(content);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    *   @param content - the file read in
    *   Splits string on newlines and does error checking on each line.
    */
    private void parseFile(String content) {
        error = false;
        String delims = "\n";
        String[] tokens = content.split(delims);
        parseFirstLine(tokens[0]);
        // Print out given time
        System.out.println(tokens[1]);
        if(error)
            return;
        blockList = new LinkedList<>();
        // Go through each line one by and and display memory information
        int size;
        totalSize = 0;
        for(int i = 2; i < tokens.length && !error; i++) {
            String[] lineTokens = tokens[i].split(SPACEDELIM);
            if(lineTokens.length != 2) {
                error("Line " + i + ": There was an issue parsing this line. Check and make sure there are the correct number of arguments");
            } else {
                // Get starting address to calculate ending address 
                if(i == 2) {
                    int converted = Integer.decode(lineTokens[0].trim());
                    heapStart = converted;
                    this.memStartLabel.setText("0x" + Integer.toHexString(converted));
                    heapEnd = converted + heapSize;
                    this.memEndLabel.setText("0x" + Integer.toHexString(heapEnd));
                }
                System.out.println(lineTokens[0] + " " + lineTokens[1]);
                int converted = Integer.decode(lineTokens[0].trim());
                //System.out.print(converted + " ");
                size = Integer.parseInt(lineTokens[1].trim());
                converted += size;
                size /= memSize;
                totalSize += size;
                blockList.add(new Block(size, false, lineTokens[0]));
                //System.out.println(converted + " ");
                if(i != tokens.length-1) {
                    if(converted != Integer.decode(tokens[i+1].split(SPACEDELIM)[0].trim())) {
                        //System.out.println(RED + "Allocated block here");
                        System.out.println("0x" + Integer.toHexString(converted) + " " + (Integer.decode(tokens[i+1].split(SPACEDELIM)[0].trim()) - converted));
                        size = (Integer.decode(tokens[i+1].split(SPACEDELIM)[0].trim()) - converted);
                        size /= memSize;
                        totalSize += size;
                        blockList.add(new Block(size, true, "0x" + Integer.toHexString(converted)));
                    }
                }       
            }
        }
        if(!error)
            System.out.println("Heap End: 0x" + Integer.toHexString(heapEnd));
        calculateTable();
        generateSquares();
        colorSquares();
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
        if(!tokens[0].toUpperCase().equals("EXPLICIT")) {            
            error("Line 1: Visualizer only supports explicit free lists. The given freelist type does not match.");
            return;
        }
        this.listTypeLabel.setText("Explicit");
        memSize = Integer.parseInt(tokens[1]);
        this.memRowLabel.setText(Integer.toString(memSize));
        heapSize = Integer.parseInt(tokens[2].trim());
        this.heapSizeLabel.setText(Integer.toString(heapSize));
        System.out.printf("%-35.35s %-35.35s %s\n", tokens[0].trim(), "Memory Row Size: " + memSize + " bytes", "Total Heap Size: " + heapSize + " bytes");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //ds.setOffsetY(5.0);
       // ds.setOffsetX(5.0);
        //ds.setColor(Color.GRAY);
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
    private void calculateTable() {
        for(grid = 0; grid * grid < totalSize; grid++);
        System.out.println("Need " + grid + " X " + grid + " size grid");
        System.out.println("Pane Width: " + this.table.getWidth() + " Pane Height: " + this.table.getHeight());
        blockWidth = this.table.getWidth()/(double)grid;
        blockHeight = this.table.getHeight()/(double)grid;
        // Ensure blocks are always squares and always can fit in the pane
        if(blockWidth > blockHeight)
            blockWidth = blockHeight;
        System.out.println("Each block should be " + blockWidth + " by " + blockWidth);
    }
    private void generateSquares() {
        rec = new Rectangle [grid][grid];
        for(int i = 0; i < grid; i++)
            for(int j = 0; j < grid; j++) {
                rec[i][j] = new Rectangle();
                rec[i][j].setX(i * blockWidth);
                rec[i][j].setY(j * blockWidth);
                rec[i][j].setWidth(blockWidth);
                rec[i][j].setHeight(blockWidth);
                rec[i][j].setFill(null);
                rec[i][j].setStroke(Color.BLACK);
                //ds.setInput(reflection);    
                rec[i][j].setEffect(ds);
                table.getChildren().add(rec[i][j]);
            }                
    }
    
    private void resizeSquares() {
        for(int i = 0; i < grid; i++)
            for(int j = 0; j < grid; j++) {
                rec[i][j].setX(i * blockWidth);
                rec[i][j].setY(j * blockWidth);
                rec[i][j].setWidth(blockWidth);
                rec[i][j].setHeight(blockWidth);
                //table.getChildren().add(rec[i][j]);
            }      
    }
    private void colorSquares(){
        Block block;
        int i = 0, j = 0, k = 0;
        int currentAddr = heapStart;
        String currentAddrs;
        while(k < blockList.size()){
            block = blockList.get(k);
            int size = block.getSize();
            while(size != 0) {
                currentAddr += memSize;
                currentAddrs = "0x" + Integer.toHexString(currentAddr);
                if(block.isAllocated()) {
                    rec[i][j].setFill(Color.RED);
                    Tooltip.install(rec[i++][j], new Tooltip(currentAddrs));
                }
                else {
                    rec[i][j].setFill(Color.GREEN);
                    Tooltip.install(rec[i++][j], new Tooltip(currentAddrs));
                }
                size--;
                if(i == grid) {
                    i = 0;
                    j++;
                }
            }
            k++;
        }
         //Tooltip.install(table, t);
        //rec[0][1].setFill(Color.GREEN);
    }
    
    public void refresh() {
        //table.getChildren().clear();
        if(rec != null) {
            calculateTable();
            resizeSquares();
            colorSquares();
        }
    }
}
