import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class instrOp {
    
    private HashMap<String, Integer> registers;
    private HashMap<String, Integer> labelMap;
    private String instruction;
    private int pc;
    // private int datamemory[];

    public instrOp(String instruction, HashMap<String, Integer> labelMap, HashMap<String, Integer> registers){
        this.labelMap = labelMap;
        this.instruction = instruction;
        this.registers = registers;
        this.pc = 0;
        //this.datamemory = datamemory;
    }

    public int get_register(String name){
        return registers.get(name);
    }

    public void set_register(String name, int value){
        registers.put(name, value);
    }

    public int get_pc(){
        return this.pc;
    }

    public void set_pc(int value){
        this.pc = value;
    }

    public HashMap<String, Integer> execute_instruction(){
        String arr[] = instruction.trim().split("\\s+");
        String instName = arr[0];

        if (instName.equals("add")){
            String destination = arr[1];
            int reg1 = get_register(arr[2]);
            int reg2 = get_register(arr[3]);
            int finnal = reg1 + reg2;
            set_register(destination, finnal);
        }

        else if (instName.equals("addi")){
            String destination = arr[1];
            int reg1 = get_register(arr[2]);
            String immediate = arr[3];
            int intimm = Integer.parseInt(immediate);
            int finnal = reg1 + intimm;
            set_register(destination, finnal);
        }
        
        else if (instName.equals("sub")){
            String destination = arr[1];
            int reg1 = get_register(arr[2]);
            int reg2 = get_register(arr[3]);
            int finnal = reg1 - reg2;
            set_register(destination, finnal);
        }

        else if (instName.equals("and")){
            String destination = arr[1];
            int reg1 = get_register(arr[2]);
            int reg2 = get_register(arr[3]);
            int finnal = reg1 & reg2;
            set_register(destination, finnal);
        }

        else if (instName.equals("or")){
            String destination = arr[1];
            int reg1 = get_register(arr[2]);
            int reg2 = get_register(arr[3]);
            int finnal = reg1 | reg2;
            set_register(destination, finnal);
        }

        else if (instName.equals("sll")){
            String destination = arr[1];
            int reg1 = get_register(arr[2]);
            int imm = Integer.parseInt(arr[3]);
            int finnal = reg1 << imm;
            set_register(destination, finnal);
        }
        else if (instName.equals("slt")){
            String destination = arr[1];
            int reg1 = get_register(arr[2]);
            int reg2 = get_register(arr[3]);
            int finnal;
            if (reg1 < reg2){
                finnal = 1;
            }
            else{
                finnal = 0;
            }
            set_register(destination, finnal);
        }
        else if (instName.equals("sw")){
            int value = get_register(arr[1]);
            int offset = Integer.parseInt(arr[2]);
            String reg1 = arr[3];
            int memoryLoc = get_register(reg1) + offset;
            lab4.datamemory[memoryLoc] = value; 
        }
        else if (instName.equals("lw")){
            String destination = arr[1];
            int offset = Integer.parseInt(arr[2]);
            String reg1 = arr[3];
            int memoryLoc = get_register(reg1) + offset;
            int value = lab4.datamemory[memoryLoc];
            set_register(destination, value);
        }
        else if (instName.equals("bne")){
            int reg1 = get_register(arr[1]);
            int reg2 = get_register(arr[2]);
            if (reg1 != reg2){
                lab4.pc = labelMap.get(arr[3]) - 1; // -1 because completing this instruction pc++
                lab4.takenflag = 1;
            }

        }
        else if (instName.equals("beq")){
            int reg1 = get_register(arr[1]);
            int reg2 = get_register(arr[2]);
            if (reg1 == reg2){
                lab4.pc = labelMap.get(arr[3]) - 1; // -1 because completing this instruction pc++
                lab4.takenflag = 1;
            }

        }
        else if (instName.equals("j")){
            int address = labelMap.get(arr[1]);
            lab4.pc = address - 1; // -1 because completing this instruction pc++
        }
        else if (instName.equals("jr")){
            int address = get_register(arr[1]);
            lab4.pc = address - 1; // -1 because completing this instruction pc++
        }
        else if (instName.equals("jal")){
            int address = get_register(arr[1]);
            // set pc to $ra first, then set pc
            set_register("$ra", lab4.pc);
            lab4.pc = address - 1; // -1 because completing this instruction pc++
        }
        
        return registers;

    }


}
