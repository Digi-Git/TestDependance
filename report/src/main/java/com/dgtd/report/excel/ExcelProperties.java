package com.dgtd.report.excel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "digitech.excel")
public class ExcelProperties {

    private String excelLocalDir;

    private int maxExport;

    public String getExcelLocalDir() {
        return excelLocalDir;
    }

    public void setExcelLocalDir(String excelLocalDir) {
        this.excelLocalDir = excelLocalDir;
    }

    public int getMaxExport() {
        return maxExport;
    }

    public void setMaxExport(int maxExport) {
        this.maxExport = maxExport;
    }
}
