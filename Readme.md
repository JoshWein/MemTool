# Memory Snapshot Visualizer Tool

This program runs in either a command line environment or a GUI environment and takes in a specific memory snapshot format and displays a visual representation of free and allocated blocks.

## NonGUI

In a NonGUI environment, this program takes the text file as a command line argument and outputs the data in a basic format colouring the words to designate free and allocated blocks.

## GUI

In a GUI environment, this program gives the user the option to load in a text file via button press. The application renders the data using blocks colored green and red to designate free and allocated blocks. Each block represents one row of memory. By hovering the mouse over a block you can see the starting memory address of that specific block. You can resize the application to make the squares bigger as well as load in a new file simply by clicking "Upload Snapshot" button.

### In Progress

Upcoming featues will include the calculation of external fragmentation as well as other possible visuals and input types/format.