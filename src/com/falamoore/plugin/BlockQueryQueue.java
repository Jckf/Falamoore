package com.falamoore.plugin;

public class BlockQueryQueue implements Runnable {

    public BlockQueryQueue() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Main.mysql.query(Main.QueryQueue.poll());
                Thread.sleep(50);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
