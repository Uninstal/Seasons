package org.uninstal.seasons.data.cleaner;

public interface SeasonDataCleanable {
    // true - удачно, false - неудачно
    boolean clean(SeasonDataCleaner cleaner);
}