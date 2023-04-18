package com.aptiv.smb.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportVO extends BaseRowModel {
    @ExcelProperty(value = "MEDEL", index = 0)
    private String model;
}
