package com.tta.moneta.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.util.Assert.notNull;

public class CsvUtils {

    public static <T> List<T> read(InputStream inputStream, Class<T> clazz) {
        notNull(inputStream, "inputStream is required");
        notNull(clazz, "clazz is required");

        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new CsvToBeanBuilder(reader)
                .withType(clazz)
                .withIgnoreLeadingWhiteSpace(true)
                .withErrorLocale(LocaleContextHolder.getLocale())
                .build()
                .parse();
    }
}
