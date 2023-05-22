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
        String prevarr[] = prevInstr.trim().split("\\s+");
        String prevInstName = prevarr[0];
        /* if isntr = j, jal, jr */
        if (instName == "j" || instName == "jal " ||  instName == "jr"){
            lab4.pipeQueue.stage(instName);
            lab4.pipeQueue.stage("stall");
        }

        /* if isntr = b taken */
        if (instName.equals("bne") || (instName.equals("beq"))) {
            lab4.pipeQueue.stage(instName);
            if (lab4.takenflag == 1){
                lab4.pipeQueue.stage("squash");
                lab4.pipeQueue.stage("squash");
                lab4.pipeQueue.stage("squash");
            }
        }
        /* if prev isntr = lw and this one uses same register */
        if (prevInstName.equals("lw")){
        
            String prevlw = prevarr[1];
            
            boolean sameRegister = false;
            for (int i = 1; i < arr.length; i++) {
                if (arr[i].equals(prevlw)) {
                    sameRegister = true;
                    break;
                }
            }
            if (sameRegister){
                lab4.pipeQueue.stage(instName);
                lab4.pipeQueue.stage("stall");
                lab4.pipeQueue.stage("nop");
                lab4.pipeQueue.stage("nop");
            }else{
                lab4.pipeQueue.stage(instName);
            }
            
        }
        
    

    }
}
