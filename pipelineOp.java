public class pipelineOp {

    private String instr;
    private String prevInstr;

    public pipelineOp(String instr, String prevInstr){
        this.instr = instr;
        this.prevInstr = prevInstr;
    }

    public void executeInstr(){
        String arr[] = instr.trim().split("\\s+");
        String instName = arr[0];
        /* if isntr = j, jal, jr */
        if (instName == "j" || instName == "jal " ||  instName == "jr"){
            lab4.pipeQueue.stage(instName);
            lab4.pipeQueue.stage("stall");
        }
        if (instName.equals("add")){
            lab4.pipeQueue.stage(instName);
            lab4.pipeQueue.stage("test stall");
        }
        /* if isntr = b taken */
        /* if prev isntr = lw and this one uses same register */
        /* if isntr = anything that doesnt cause delay */
        else {
            lab4.pipeQueue.stage(instName);
        }
    }

}
