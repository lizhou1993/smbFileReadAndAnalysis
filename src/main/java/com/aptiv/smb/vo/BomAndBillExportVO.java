package com.aptiv.smb.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BomAndBillExportVO extends BaseRowModel {
    @ExcelProperty(value = "model", index = 0)
    private String model;
    @ExcelProperty(value = "order", index = 1)
    private String order;
    @ExcelProperty(value = "type", index = 2)
    private String type;
    @ExcelProperty(value = "bomId", index = 3)
    private String bomId;
    @ExcelProperty(value = "createTime", index = 4)
    private String createTime;
}
