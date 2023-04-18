package com.aptiv.smb.controller;

import com.aptiv.smb.service.SmbService;
import com.aptiv.smb.util.ExportExcelUtil;
import com.aptiv.smb.vo.ExportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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

    @ApiOperation("执行解析并导出结果")
    @GetMapping("do")
    public void doIt(HttpServletResponse response) throws IOException {
        List<ExportVO> data = smbService.doIt();
        ExportExcelUtil.exportExcel(response, "BTO归档文件导出 " + LocalDate.now(), data, ExportVO.class);
    }
}
