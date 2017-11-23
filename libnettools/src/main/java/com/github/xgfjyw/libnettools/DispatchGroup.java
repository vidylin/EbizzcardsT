package com.github.xgfjyw.libnettools;

import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hongxiliang2 on 22/9/2016.
 */
public class DispatchGroup {
    private int count = 0;
    private Runnable cb;
    private BlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();
    private ThreadPoolExecutor executor;

    public DispatchGroup() {
        super();
        int n_core = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(n_core, n_core, 10, TimeUnit.SECONDS, queue);
    }

    public void add(final Runnable block) {
        enter();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                block.run();
                leave();
            }
        });
    }

    public void finish(final Runnable block) {
        cb = block;
    }


    private synchronized void enter() {
        count++;
    }

    private synchronized void leave() {
        count--;
        notifyGroup();
    }

    private void notifyGroup() {
        if (count <= 0 && cb != null) {
            executor.shutdown();
            cb.run();
        }
    }
}
