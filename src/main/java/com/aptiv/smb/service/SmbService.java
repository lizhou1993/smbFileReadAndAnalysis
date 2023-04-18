package com.aptiv.smb.service;

import com.aptiv.smb.util.DomParsingUtil;
import com.aptiv.smb.util.SmbReaderUtil;
import com.aptiv.smb.util.UnZipUtil;
import com.aptiv.smb.vo.ExportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmbService {
    private final SmbReaderUtil smbReaderUtil;
    public List<ExportVO> doIt() {
        // 读取全部文件
        List<String> filePaths = smbReaderUtil.smbReader();
        log.info("压缩包数量共计:" + filePaths.size() + "个");

        // 解压zip，获取压缩包下的所有xml文件字符串
        List<String> xmlStrs = UnZipUtil.unzipFile(filePaths);
        log.info("xml数量共计:" + xmlStrs.size() + "个");

        // dom解析xml，返回需要数据集
        List<ExportVO> exportVOList = DomParsingUtil.parse(xmlStrs);

        return exportVOList;
    }
}
