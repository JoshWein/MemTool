# Memory Snapshot Visualizer Tool

This program runs in either a command line environment or a GUI environment and takes in a specific memory snapshot format and displays a visual representation of free and allocated blocks.

For the most part the program assumes the file it's getting is in the correct format but it will warn against incorrect list types, incorrect amounts of arguments and checks a few other parameters.

## Installation

This program requires Java 1.8 on any system it runs on GUI or NonGUI.

## NonGUI

To run, simply run ```MemTool.jar mysnapshot.txt``` and make sure to have the command line argument pointing towards the snapshot you want to analyze.

In a NonGUI environment, this program takes the file as a command line argument and outputs the data in a basic format colouring the words to designate free and allocated blocks.

## GUI

To run, run MemTool.jar and click "Upload Snapshot" to choose a file to upload.

In a GUI environment, this program gives the user the option to load in a file via button press. The application renders the data using blocks colored green and red to designate free and allocated blocks. Each block represents one row of memory. By hovering the mouse over a block you can see the starting memory address of that specific block. You can resize the application to make the squares bigger as well as load in a new file simply by clicking "Upload Snapshot" button.

## Snapshot Layout

The snapshot file should be laid out as follows:
```ListType MemoryRowSize HeapSize
# Time
MemoryAddress Size
MemoryAddress Size
MemoryAddress Size
...```

### Example Snapshot Layout

```Explicit 16 16384
# 11/2/12 - 8:02PM
0x11e9030 4096
0x11eA030 3096
0x11eC030 4096```

## In Progress

Upcoming featues may include:
* The calculation of external fragmentation
* Other possible visuals.
* Other types of input types/formats.
* More types of format error checking.

