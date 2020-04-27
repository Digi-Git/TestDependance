package com.dgtd.common.csv;

import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.DataException;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvUtil {

    private final Logger log = LoggerFactory.getLogger(CsvUtil.class);

    private final ErrorService errorService;

    public CsvUtil(ErrorService errorService) {
        this.errorService = errorService;
    }

    public <T> List<T> convertFromCsv(File file, Class<? extends T> clazz)throws IOException {
        InputStream inputStream = new FileInputStream(file);

        List<T> list;


            try (Reader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8))) {

                HeaderColumnNameMappingStrategy<T> strategy
                        = new HeaderColumnNameMappingStrategy<>();
                strategy.setType(clazz);

                CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                        .withType(clazz)
                        .withMappingStrategy(strategy)
                        .withSeparator(';')
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
               list = csvToBean.parse();



        }catch (Exception e){
                String error = "Erreur de mapping csv "+ e.getLocalizedMessage();
                log.error(error);
                errorService.insertError(new ErrorObject(TypeError.CSV_MAPPING, error),false);
                throw new DataException(error);
            }


        return list;
    }



}
