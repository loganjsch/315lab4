// Garrett Green
// Logan Schwarz
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

public class lab4 {

    public static int[] datamemory = new int[8192];
    public static int pc = 0;
    public static String[] pipeline = new String[4];
    public static int cyclecount = 0;
    public static pipelineQueue pipeQueue = new pipelineQueue();

    // This function finds and maps labels in the code 
    public static HashMap<String, Integer> mapLabels(String fname){
        
        // Initialize
        File infile = new File(fname);
        if (!infile.isFile()) {
            System.out.println(fname + " is not a file!");
            return null;
        }

        HashMap<String, Integer> labelMap = new HashMap<>();
        int lineCount = 0;

        // first pass to find labels, save name and line number into table 
        try {
            Scanner scanner = new Scanner(infile);
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine().trim(); // removes whitespace

                if (line.startsWith("#")){
                    continue;
                }

                int colonIndex = line.indexOf(":"); // looks for colon

                // extracts the data from before the colon and then puts it into hashmap with lineCount and label
                if (colonIndex >= 0) {
                    String label = line.substring(0, colonIndex);
                    labelMap.put(label, lineCount);
                    lineCount++;
                } else {
                    int commentIndex = line.indexOf("#");
                    if (commentIndex >= 0) {
                        line = line.split("#")[0].trim(); // remove any text after the comment
                    }
                    if (!line.isEmpty() || line.startsWith("#")) { // check if line is not empty
                        lineCount++;
                    }
                }
            }
            scanner.close();    
        }
        catch (FileNotFoundException e) {
            System.out.println("Error.");
            e.printStackTrace();
        }

        return labelMap;
    }

    // This function converts asm file into a usable array of asm lines
    public static String[] readASM(String fname){
        

        // Initialize
        File infile = new File(fname);
        if (!infile.isFile()) {
            System.out.println(fname + " is not a file!");
            return null;
        }
        StringBuilder output = new StringBuilder();
        
        // second pass 
        try {
            Scanner scanner = new Scanner(infile); 

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("#")){
                    continue;
                }

                boolean inlinecomment = false;
                boolean leadingwhitespace = true;

                int colonIndex = line.indexOf(":");
                if (colonIndex >= 0) {
                    // Skip the label in the code
                    line = line.substring(colonIndex + 1).trim();
                }
                // If the line is a comment, dont include
                for (int i = 0; i < line.length(); i++){
                    char c = line.charAt(i);
                    if (c == '#') {
                        inlinecomment = true;
                    }
                    if (Character.isWhitespace(c)) {
                        if (leadingwhitespace){
                            // ignore whitespace
                        }
                        else {
                            output.append(' ');
                            leadingwhitespace = true;
                        }
                    } else if (c == ',' || c == '(' || c == ')') {
                        output.append(' ');
                    } else if (c == '$' && !inlinecomment) {
                            output.append(' ');
                            output.append(c);
                            leadingwhitespace = false;
                    } else if (!inlinecomment) {
                        output.append(c);
                        leadingwhitespace = false;
                    }
                }
                // Newline
                output.append('\n');
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error.");
            e.printStackTrace();
        }

        // building and prepping the output array
        String[] outputArray = output.toString().split("\\r?\\n");
        outputArray = Arrays.stream(outputArray).filter(s -> !s.trim().isEmpty()).toArray(String[]::new);
        return outputArray;
    }

    // This function initializes our registers hashmap
    public static HashMap<String, Integer> createRegistersMap(){
        HashMap<String, Integer> registers = new HashMap<String, Integer>();

        registers.put("$0", 0);
        registers.put("$v0", 0);
        registers.put("$v1", 0);
        registers.put("$a0", 0);
        registers.put("$a1", 0);
        registers.put("$a2", 0);
        registers.put("$a3", 0);
        registers.put("$t0", 0);
        registers.put("$t1", 0);
        registers.put("$t2", 0);
        registers.put("$t3", 0);
        registers.put("$t4", 0);
        registers.put("$t5", 0);
        registers.put("$t6", 0);
        registers.put("$t7", 0);
        registers.put("$s0", 0);
        registers.put("$s1", 0);
        registers.put("$s2", 0);
        registers.put("$s3", 0);
        registers.put("$s4", 0);
        registers.put("$s5", 0);
        registers.put("$s6", 0);
        registers.put("$s7", 0);
        registers.put("$t8", 0);
        registers.put("$t9", 0);
        registers.put("$sp", 0);
        registers.put("$ra", 0);  
        
        return registers;
    }

    public static void printPipeline(){
        System.out.println("if/id	id/exe	exe/mem	mem/wb");
        for (String register : pipeline) {
			System.out.print(register + "    ");
		}
        System.out.println();
    }

    public static void insertInPipe(String instName){
        pipeline[3] = pipeline[2];
        pipeline[2] = pipeline[1];
        pipeline[1] = pipeline[0];
        pipeline[0] = instName;
        cyclecount++;
    }


    public static void main(String[] args) throws FileNotFoundException, IOException{

        String[] asmarray = readASM(args[0]);
        HashMap<String, Integer> labelMap = mapLabels(args[0]);
        HashMap<String, Integer> registers = createRegistersMap();
        Arrays.fill(pipeline, "empty");
    
        // registers are null going into this 
        if (args.length == 1) {
            // interactive
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("mips> ");
                String input = scanner.nextLine();
                String[] userIn = input.split(" ");
                
                if (userIn.length == 1){
                    if (userIn[0].equals("h")){
                        // help
                        System.out.println("List of commands:\n" +
                                           "h = show help\n" + 
                                           "d = dump register state\n" +
                                           "s = single step through the program (i.e. execute 1 instruction and stop) s num = step through num instructions of the program\n" +
                                           "r = run until the program ends\n" +
                                           "m num1 num2 = display data memory from location num1 to num2\n" +
                                           "c = clear all registers, memory, and the program counter to 0\n" +
                                           "q = exit the program");
                    }
                    else if (userIn[0].equals("d")){
                        // dump registers
                        System.out.println("pc = " + pc);
                        int count = 1;
                        for (Entry<String, Integer> entry : registers.entrySet()) {
                            System.out.print(entry.getKey() + " = " + entry.getValue() + "    ");
                            if (count == 3){
                                System.out.println();
                                count = 0;
                            }
                            count++;
                        }
                    }
                    else if (userIn[0].equals("p")){
                        // print pipeline 
                        printPipeline();
                    }
                    else if (userIn[0].equals("s")){
                        if (pc < asmarray.length){
                            if (pipeQueue.isEmpty()){
                                instrOp operation = new instrOp(asmarray[pc], labelMap, registers);
                                registers = operation.execute_instruction();
                                System.out.println("1 instruction completed.");
    
                                if (pc == 0){
                                    pipelineOp firstPipeOp = new pipelineOp(asmarray[pc], "doesnotexist");
                                    firstPipeOp.executeInstr();
                                }
                                else {
                                    pipelineOp pipeOp = new pipelineOp(asmarray[pc], asmarray[pc - 1]);
                                    pipeOp.executeInstr();
                                }
                                pc++;
    
                            }
                            pipeQueue.printQueue();
                            insertInPipe(pipeQueue.getFirst());
                            System.out.println("Cycle count is at: " + cyclecount);
                            System.out.println("Pc is at: " + pc);
                            printPipeline();
                        }
                        else {
                            System.out.println("Program finished. Please clear.");
                        }
                    }
                    else if (userIn[0].equals("r")){
                        // run whole program
                        int count = 0;
                        while (pc < asmarray.length) {
                            if (pipeQueue.isEmpty()){
                                instrOp operation = new instrOp(asmarray[pc], labelMap, registers);
                                registers = operation.execute_instruction();
                                System.out.println("1 instruction completed.");
    
                                if (pc == 0){
                                    pipelineOp firstPipeOp = new pipelineOp(asmarray[pc], "doesnotexist");
                                    firstPipeOp.executeInstr();
                                }
                                else {
                                    pipelineOp pipeOp = new pipelineOp(asmarray[pc], asmarray[pc - 1]);
                                    pipeOp.executeInstr();
                                }
                                pc++;
                                count++;
    
                            }
                            pipeQueue.printQueue();
                            insertInPipe(pipeQueue.getFirst());
                            System.out.println("Cycle count is at: " + cyclecount);
                            System.out.println("Pc is at: " + pc);
                            printPipeline();
                            
                        }
                        System.out.println("Program Completed. Please clear.");
                        System.out.println(cyclecount + " cycle(s) completed.");
                        System.out.println(count + " instruction(s) completed.");            
                    }

                    else if (userIn[0].equals("c")){
                        // clear registers, memory (pc == 0)
                        for (Entry<String, Integer> entry : registers.entrySet()) {
                            entry.setValue(0);
                        }
                        for (int i = 0; i < datamemory.length; i++) {
                            datamemory[i] = 0;
                        }
                        pc = 0;
                        cyclecount = 0;
                        Arrays.fill(pipeline, "empty");
                    }
                    else if (userIn[0].equals("q")){
                        // exit program 
                        System.exit(0);
                    }
                } else if (userIn.length == 2 && userIn[0].equals("s")){
                    // code for s num
                    int count = 0;
                    while (count < Integer.parseInt(userIn[1])){
                        if (pc < asmarray.length){ 
                            instrOp operation = new instrOp(asmarray[pc], labelMap, registers);
                            registers = operation.execute_instruction();
                            pc++;
                            count++;
                        }
                        else {
                            System.out.println("Reached the end of program. Please clear.");
                            count = Integer.parseInt(userIn[1]);
                        }       
                    }
                    System.out.println(userIn[1] + " instruction(s) completed.");
                } else if (userIn.length == 3 && userIn[0].equals("m")){
                    // code for m num1 num2
                    int lower = Integer.parseInt(userIn[1]);
                    int upper = Integer.parseInt(userIn[2]);
                    for (int i = lower; i <= upper; i++) {
                        System.out.println("[" + i + "] = " + datamemory[i]);
                    }
                }
            }
        }
        else{
            System.out.println("Incorrect arguments passed!");
            return;
        }
    }    
}