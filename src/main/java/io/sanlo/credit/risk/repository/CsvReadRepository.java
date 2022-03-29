package io.sanlo.credit.risk.repository;

import java.util.List;

/**
 * Read Repository
 * @param <T>
 */
public interface CsvReadRepository<T> {
    /**
     * Read from a CSV File
     * @return List of Generics T
     */
    List<T> read();
}