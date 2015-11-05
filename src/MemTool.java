import java.awt.GraphicsEnvironment;
import java.io.IOException;
import static java.lang.System.exit;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *  CSE320 Fall 2015 - Extra Credit: Memory Snapshot Visualizer Tool
 * @author Josh Wein
 */
public class MemTool {
    
    /**
     * @param args the command line arguments - file to be parsed and visualized
     *  Runs NonGui or Gui version based upon the type of system the application is running on.
     */
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) { // Checks if it's a GUI system
            new NonGui(args);
        } else {
            //new NonGui(args);
            javafx.application.Application.launch(Gui.class);
        }
    }
}

/*
*   Simple non-gui application for visualizing memory. Analyzes given explicit free list snapshot
*   and outputs a calculated list showing free and allocated blocks as well as the address 
*   of the end of the heap.
*/
class NonGui {
    // Initialize default printing colors.
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[1;35;40m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String SPACEDELIM = "[ ]+";
    // Global size variables
    public static int memSize;
    public static int heapSize;
    public static int heapEnd;
    
    NonGui(String [] args) {
        run(args);
    }
    public void run(String []args) {
        System.out.println(PURPLE + "Running Memory Snapshot Visualizer Tool" + RESET);
        System.out.println(GREEN + "GREEN = FREE\n" + RED + "RED = ALLOCATED");
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(args[0])));
            parseFile(content);
        } catch (IOException ex) {
            System.out.print(RED);
            System.out.println("ERROR: File not found. Make sure the argument points to the snapshot file.");
        }
        endProgram(0);
    }
    
    /*
    *   @param content - the file read in
    *   Splits string on newlines and does error checking on each line.
    */
    private static void parseFile(String content) {
        String delims = "\n";
        String[] tokens = content.split(delims);
        parseFirstLine(tokens[0]);
        // Print out given time
        System.out.println(tokens[1]);
        // Go through each line one by and and display memory information
        for(int i = 2; i < tokens.length; i++) {
            String[] lineTokens = tokens[i].split(SPACEDELIM);
            // Get starting address to calculate ending address 
            if(i == 2) {
                int converted = Integer.decode(lineTokens[0].trim());
                heapEnd = converted + heapSize;
            }
            if(lineTokens.length != 2)
                error(i + 1);
            System.out.println(GREEN + lineTokens[0] + " " + lineTokens[1]);
            int converted = Integer.decode(lineTokens[0].trim());
            converted += Integer.parseInt(lineTokens[1].trim());
            if(i != tokens.length-1) {
                if(converted != Integer.decode(tokens[i+1].split(SPACEDELIM)[0].trim())) {
                    System.out.println(RED + "0x" + Integer.toHexString(converted) + " " + (Integer.decode(tokens[i+1].split(SPACEDELIM)[0].trim()) - converted));
                }
            }            
        }
        System.out.println(BLUE + "Heap End: 0x" + Integer.toHexString(heapEnd));
    }
    
    /*
    *   Method for parsing and checking the first line of a snapshot file.
    *   @param  line - line to be parsed
    */
    private static void parseFirstLine(String line) {
        String[] tokens = line.split(SPACEDELIM);
        if(tokens.length != 3) {
            System.out.print(RED);
            System.out.println("ERROR: First line does not have 3 arguments. Should be: 'ListType', 'MemRowSize', 'TotalHeapSize'");
            endProgram(1);
        }
        // Parse and print first line
        if(!tokens[0].toUpperCase().equals("EXPLICIT")) {
            System.out.print(RED);
            System.out.println("ERROR: Line 1: Visualizer only supports explicit free lists. The given freelist does not match.");
            endProgram(1);
        }
        memSize = Integer.parseInt(tokens[1]);
        heapSize = Integer.parseInt(tokens[2].trim());
        System.out.printf(CYAN + "%-35.35s %-35.35s %s\n", tokens[0].trim(), "Memory Row Size: " + memSize + " bytes", "Total Heap Size: " + heapSize + " bytes");
    }
    
    private static void endProgram(int exitCode) {
        System.out.print(RESET);
        exit(exitCode);
    }
    
    // For errors while parsing the main portion of the file
    private static void error(int lineNum){
        System.out.println(RED + "ERROR: Line " + lineNum + ": There was an issue parsing this line. Check and make sure there are the correct number of arguments");
        endProgram(1);
    }
}