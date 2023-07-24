package org.uninstal.seasons.util;

public interface MessageReplacer {
    
    MessageReplacer NONE = original -> original;
    String format(String original);
}