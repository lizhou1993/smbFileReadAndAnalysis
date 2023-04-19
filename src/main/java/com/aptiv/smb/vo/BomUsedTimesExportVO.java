package com.aptiv.smb.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BomUsedTimesExportVO extends BaseRowModel {
    @ExcelProperty(value = "bomId", index = 0)
    private String bomId;
    @ExcelProperty(value = "usedTimes", index = 1)
    private Integer usedTimes;
    @ExcelProperty(value = "createTime", index = 2)
    private String createTime;
}
