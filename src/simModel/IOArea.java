package simModel;

import java.util.concurrent.ArrayBlockingQueue;

public class IOArea extends ArrayBlockingQueue<Bin> {

    public IOArea(int capacity) {
        super(capacity);
    }
}
