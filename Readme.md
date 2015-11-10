# Memory Snapshot Visualizer Tool

This program runs in either a GUI environment or a command line environment and takes in a specific memory snapshot format and displays a visual representation of the heap.

For the most part the program assumes the file it's getting is in the correct format but it will warn against incorrect list types, incorrect amounts of arguments and a few other parameters.

## Installation

This program requires Java 1.8 on any system it runs on GUI or NonGUI.

## NonGUI

To run, simply run ```MemTool.jar mysnapshot.txt``` and make sure to have the command line argument pointing towards the snapshot you want to analyze.

In a NonGUI environment, this program takes the file as a command line argument and outputs the data in a basic format colouring the words to designate free and allocated blocks.

This version of the program only runs a very basic analyzation of an explicit free list, for all other functionality, run the GUI version.

## GUI

To run, run MemTool.jar and click "Upload Snapshot" to choose a file to upload.

In a GUI environment, this program gives the user the option to load in a file via button press. The application renders the data using blocks colored green and red to distinguish between free and allocated blocks. Each block represents one row of memory. By hovering the mouse over a block you can see the starting memory address of that specific block and the sections total size. You can resize the application to make the squares bigger as well as load in a new file simply by clicking "Upload Snapshot" button.

When hovering over a specific section, the previous and next sections will be higlighted orange and blue respectively.

For all lists the application calculates external fragmentation and displays it in a pie chart. For a Full Memory Information list the application also calculates internal fragmentation and displays the total amount in a seperate pie chart based off of extra information. See [sample layout](#Full-Memory-Information-Snapshot-Layout).

## Explicit/Implicit Freelist Snapshot Layout

The snapshot file should be laid out as follows:

```
ListType MemoryRowSize HeapSize
# Time
MemoryAddress Size
MemoryAddress Size
MemoryAddress Size
...
```

### Example Explicit/Implicit Freelist Snapshot Layout

```
Explicit 16 2304
# 11/2/12 - 8:02PM
0x11e9430 256
0x11e9130 256
0x11e9730 128
0x11e9330 256
0x11e9030 256
0x11e9630 256
```

```
Implicit 16 2304
# 11/2/12 - 8:02PM
0x11e9430 256
0x11e9130 256
0x11e9730 128
0x11e9330 256
0x11e9030 256
0x11e9630 256
```

## Full Memory Information Snapshot Layout

For a Full Memory Information Snapshot, it's similar to the Explicit/Implicit snapshot except that for allocated blocks you need to put the amount of requested space.

```
ListType MemoryRowSize HeapSize
# Time
AllocatedMemoryAddress 	Size 	RequestedSize
FreeMemoryAddress 		Size 	
AllocatedMemoryAddress 	Size 	RequestedSize
...
```

### Example Full Memory Information Snapshot Layout

```
Implicit 16 4608
# 11/2/12 - 8:02PM
0x11e9430 256 128
0x11e9130 256 256
0x11e9730 128 128
0x11e9330 256 256
0x11e9830 256
0x11e9030 256 200
0x11e9A30 128 100
0x11e9630 256 200
```

## Requirements Fullfillment

### Minimum visual requirements (30 pts):

Starting address of each allocated or free region of memory
* Marking/coloring/etc for each allocated and free region of memory
  * Allocated = Red
  * Free = Green
* Legend to explain the marking/coloring
  * Legend in top right gives small explanation for colors.
* Size of the allocated or free region of memory
  * Hovering over a block shows the size of the whole memory section.
* Display the full range of memory in the allocated heap
  * Size is shown in the sidebar as well as the blocks visually spanning the allocated heap.

### Additional visual requirements (5 pts):
* Calculate and display the amount of external fragmentation
  * All 3 lists have external fragmentation calculated and displayed in a pie chart.
* Display links between free blocks (Explicit free lists)
  * Explicit and Implicit lists have the previous and next blocks of a free block highlighted orange and blue respectively.
  * Full Memory Information highlights the previous and next blocks for both free and allocated blocks.

### Advanced requirements (15 pts): 
* Create a input format for implicit allocators and/or full memory information (free and allocated blocks) using the HW3 format as a starting point.
  * Explicit format works for implicit lists.
* Calculate and display the amount of internal fragmentation.
  * Full Memory Information lists will display Requested vs. Recieved for internal fragmentation as a pie chart in the sidebar.
  * Requested percentage is how much of the allocated space was used.
  * Recieved percentage is the leftover space ie. internal fragmentation.
* Mark/color/etc the internal fragmentation in each block.

