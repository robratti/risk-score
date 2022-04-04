package io.rratti.credit.risk.config;

import com.opencsv.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Configuration
public class CsvConfig {
    @Bean
    public CSVReader companyReader(@Value("#{csvProperties.companyFileName}") String fileName) throws URISyntaxException, IOException {
        var reader = Files.newBufferedReader(Paths.get(
                ClassLoader.getSystemResource(fileName).toURI()
        ));

        return getCsvReader(reader);
    }

    @Bean
    public CSVReader financialMetricsReader(@Value("#{csvProperties.metricsFileName}") String metricFileName) throws URISyntaxException, IOException {
        var reader = Files.newBufferedReader(Paths.get(
                ClassLoader.getSystemResource(metricFileName).toURI()
        ));

        return getCsvReader(reader);
    }

    private CSVReader getCsvReader(BufferedReader reader) {
        return new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(this.getCsvParser())
                .build();
    }

    @Bean
    public CSVWriter reportWriter(@Value("#{csvProperties.financialReportFileName}") String reportFileName) throws IOException {
        Path path = Paths.get(new FileSystemResource("").getFile().getAbsolutePath(), reportFileName);

        return new CSVWriter(
                new FileWriter(path.toString()),
                ',',
                ICSVWriter.NO_QUOTE_CHARACTER,
                ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                ICSVWriter.DEFAULT_LINE_END
        );
    }

    @Bean
    public SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    private CSVParser getCsvParser() {
        return new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .build();
    }
}
