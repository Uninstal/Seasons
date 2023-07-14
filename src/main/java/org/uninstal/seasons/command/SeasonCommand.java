package org.uninstal.seasons.command;

public abstract class SeasonCommand {
    
    private final int args;

    public SeasonCommand(int args) {
        this.args = args;
    }

    public int getArgs() {
        return args;
    }
}