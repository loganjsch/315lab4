import java.util.LinkedList;

public class pipelineQueue {
    private LinkedList<String> queue;
    
    public pipelineQueue(){
        queue = new LinkedList<>();
    }

    public void stage(String element){
        queue.addLast(element);
    }

    public String getFirst(){
        if(queue.isEmpty()){
            throw new IllegalStateException("Queue is empty.");
        }
        return queue.removeFirst();
    }

    public void printQueue(){
        for (String element : queue){
            System.out.println(element);
        }
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
