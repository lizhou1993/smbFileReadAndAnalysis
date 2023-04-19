package com.aptiv.smb.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BomModuleExportVO extends BaseRowModel {
    @ExcelProperty(value = "bomId", index = 0)
    private String bomId;
    @ExcelProperty(value = "type", index = 1)
    private String type;
    @ExcelProperty(value = "partNo", index = 2)
    private String partNo;
    @ExcelProperty(value = "createTime", index = 3)
    private String createTime;
}
