package io.sanlo.credit.risk.repository;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.util.List;

public interface CsvWriteRepository<T> {
    List<T> write(List<T> csvRecord) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException;
}
