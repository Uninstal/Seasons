package org.uninstal.seasons.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SeasonEvent extends Event {
    
    private final static HandlerList handlerList = new HandlerList();
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}