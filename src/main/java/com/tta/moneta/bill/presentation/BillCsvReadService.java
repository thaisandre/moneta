package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.domain.Bill;
import com.tta.moneta.utils.CsvUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.util.Assert.*;

@Service
public class BillCsvReadService {

    public List<Bill> toBill(MultipartFile file) {
        return readCsv(file).stream().map(DtoBillImportRow::toBill).toList();
    }

    private List<DtoBillImportRow> readCsv(MultipartFile file) {
        notNull(file, "file is required");
        isTrue(!file.isEmpty(), "file cannot be empty");
        isTrue(file.getContentType().equals("text/csv"), "file must be a csv");
        try {
            return CsvUtils.read(file.getInputStream(), DtoBillImportRow.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
