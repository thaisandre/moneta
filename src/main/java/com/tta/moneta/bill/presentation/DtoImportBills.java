package com.tta.moneta.bill.presentation;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

record DtoImportBills(@NotNull MultipartFile file){

    public boolean hasEmptyFile() {
        return Optional.ofNullable(file).map(MultipartFile::isEmpty).orElse(true);
    }

    public boolean hasCsvFile() {
        return Optional.ofNullable(file).map(MultipartFile::getContentType).map("text/csv"::equals).orElse(false);
    }


}
