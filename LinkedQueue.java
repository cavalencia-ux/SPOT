import java.util.ArrayList;

/**
 * Custom Queue implementation
 */
public class LinkedQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;
    
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    public LinkedQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }
    
    /**
     * Add element to queue
     */
    public void enqueue(T element) {
        Node<T> newNode = new Node<>(element);
        
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }
    
    /**
     * Remove and return front element
     */
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        
        T data = front.data;
        front = front.next;
        size--;
        
        if (isEmpty()) {
            rear = null;
        }
        
        return data;
    }
    
    /**
     * View front element without removing
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return front.data;
    }
    
    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return front == null;
    }
    
    /**
     * Get queue size
     */
    public int size() {
        return size;
    }
    
    /**
     * Get all elements in queue as ArrayList
     */
    public ArrayList<T> getAllWaiting() {
        ArrayList<T> list = new ArrayList<>();
        Node<T> current = front;
        
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        
        return list;
    }
}
