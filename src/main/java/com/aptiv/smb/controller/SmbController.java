package com.aptiv.smb.controller;

import com.aptiv.smb.service.SmbService;
import com.aptiv.smb.util.ExportExcelUtil;
import com.aptiv.smb.vo.BomAndBillExportVO;
import com.aptiv.smb.vo.BomModuleExportVO;
import com.aptiv.smb.vo.BomUsedTimesExportVO;
import com.aptiv.smb.vo.DataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@Api(tags = "BTO归档文件。\n" +
        "实现思路：\n" +
        "1，访问并获取共享盘上的所有文件（压缩包）；\n" +
        "2，解压缩包，并读取压缩包内的全部xml文件；\n" +
        "3，解析xml文件，读取需要的字段并加入到list中；\n" +
        "4，将list内容保存到excel并导出；")
@RequiredArgsConstructor
@RestController
@RequestMapping("smb")
class SmbController {
    private final SmbService smbService;

    @ApiOperation("执行解析并导出excel")
    @GetMapping("do")
    public void doIt(HttpServletResponse response) throws IOException {
        DataVO data = smbService.doIt();
        ExportExcelUtil.exportExcel(response, "K426 BOM记录报表" + LocalDate.now(), data.getBomUsedTimesExportVOS(), BomUsedTimesExportVO.class, data.getBomModuleExportVOS(), BomModuleExportVO.class, data.getBomAndBillExportVOS(), BomAndBillExportVO.class);
    }

    @ApiOperation("执行解析将excel保存到C盘下")
    @GetMapping("do1")
    public void doIt1() throws Exception {
        DataVO data = smbService.doIt1();
        ExportExcelUtil.saveExcel("K426 BOM记录报表" + LocalDate.now(), data.getBomUsedTimesExportVOS(), BomUsedTimesExportVO.class, data.getBomModuleExportVOS(), BomModuleExportVO.class, data.getBomAndBillExportVOS(), BomAndBillExportVO.class);
    }

}
