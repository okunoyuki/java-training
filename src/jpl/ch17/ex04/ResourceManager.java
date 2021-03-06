package jpl.ch17.ex04;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

public final class ResourceManager {
    final ReferenceQueue<Object> queue;
    final Map<Reference<?>, Resource> refs;
    final Thread reaper;
    boolean shutdown = false;
    
    public ResourceManager() {
        queue = new ReferenceQueue<Object>();
        refs = new HashMap<Reference<?>, Resource>();
        reaper = new ReaperThread();
        reaper.start();
        
        // Do something.
    }
    
    public synchronized void shutdown() {
        if (!shutdown) {
            shutdown = true;
            reaper.interrupt();
        }
    }
    
    public synchronized Resource getResource(Object key) {
        if (shutdown)
            throw new IllegalArgumentException();
            
        Resource res = new ResourceImpl(key);
        Reference<?> ref = new PhantomReference<Object>(key, queue);
        refs.put(ref, res);
        return res;
    }
        
    class ReaperThread extends Thread {
        @Override
        public void run() {
            while(!shutdown || refs.size() > 0) {
                try {
                    Reference<?> ref = queue.remove();
                    Resource res = null;
                    synchronized (ResourceManager.this) {
                        res = refs.get(ref);
                        refs.remove(ref);
                    }
                    res.release();
                    ref.clear();
                } catch (InterruptedException e) {
                    shutdown = true;
                    //44行目で同期を取らずshutdownにアクセスしている。この行がないと危ないことになっていた。
                    //shutdownをvolatile宣言することでも回避できる
                }
            }
        }
    }
}
