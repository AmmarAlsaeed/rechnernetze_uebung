package de.ipvs.rni.exercise2.common;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;

public class GlobalClock
{
    private int timestamp;
    private int interval;

    private ArrayList<ProcessEvents> eventListeners = new ArrayList<>();

    public GlobalClock()
    {
        timestamp = 0;
        interval = 500;
    }

    public int getTime()
    {
        return timestamp;
    }

    public void registerCallback(ProcessEvents eventListener)
    {
        eventListeners.add(eventListener);
    }

    public void tempusFugit() throws InterruptedException
    {
        Iterator<ProcessEvents> it = eventListeners.iterator();

        while(it.hasNext())
        {
            it.next().process();
        }

        Thread.sleep(interval);
        timestamp++;
    }
}