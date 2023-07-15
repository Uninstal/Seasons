package org.uninstal.seasons.service.cleaner;

public interface SeasonDataCleanable {
    // true - удачно, false - неудачно
    boolean clean(SeasonDataCleaner cleaner);
}