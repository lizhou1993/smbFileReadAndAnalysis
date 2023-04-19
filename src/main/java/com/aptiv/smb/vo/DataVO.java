package com.aptiv.smb.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DataVO {
    private List<BomUsedTimesExportVO> bomUsedTimesExportVOS;
    private List<BomModuleExportVO> bomModuleExportVOS;
    private List<BomAndBillExportVO> bomAndBillExportVOS;
}
