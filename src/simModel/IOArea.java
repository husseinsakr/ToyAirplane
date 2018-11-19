package simModel;

import java.util.concurrent.ArrayBlockingQueue;

public class IOArea extends ArrayBlockingQueue<Bin> {
    public int n; // we dont need it

    public IOArea(int capacity) {
        super(capacity);
    }
}
