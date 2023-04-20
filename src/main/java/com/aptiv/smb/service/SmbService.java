package com.aptiv.smb.service;

import com.aptiv.smb.config.SmbAutoProperties;
import com.aptiv.smb.util.DomParsingUtil;
import com.aptiv.smb.util.SmbReaderUtil;
import com.aptiv.smb.util.UnZipUtil;
import com.aptiv.smb.vo.*;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmbService {
    private final SmbReaderUtil smbReaderUtil;
    private final SmbAutoProperties smbAutoProperties;

    /**
     * 执行此方法需要设置堆内存，否则可能会堆内存溢出
     * -Xms10240m -Xmx10240m
     * */
    public DataVO doIt() {
        // 读取全部文件
        List<String> filePaths = smbReaderUtil.smbReader();
        log.info("压缩包数量共计:" + filePaths.size() + "个");

        Map<String, List<BomModuleExportVO>> bomModuleMap = new HashMap<>(); // bom模块号配置
        Map<String, Integer> bomUsedTimesMap = new HashMap<>(); // bom使用次数map
        List<BomAndBillExportVO> bomAndBillExportVOList = new ArrayList<>(); // bom订单信息

        int count = 0;
        for(String filePath : filePaths) {
            log.info("正在执行第:" + (++count) + "个");
            List<String> xmlStrs = UnZipUtil.unzipFile(filePath); // 解压zip，获取压缩包下的所有xml文件字符串， 一个压缩包文件返回多个xml文件
            for (String xmlStr : xmlStrs) { // dom解析xml，返回需要数据集
                List<BomAndBillDetailVO> bomAndBillDetailVOS = DomParsingUtil.parse(xmlStr); // 一个xml解析多个工单bom
                for (BomAndBillDetailVO vo : bomAndBillDetailVOS) {
                    BomAndBillExportVO bomAndBillExportVO = new BomAndBillExportVO();
                    bomAndBillExportVO.setModel(vo.getModel());
                    bomAndBillExportVO.setOrder(vo.getOrder());
                    bomAndBillExportVO.setCreateTime(vo.getCreateTime());
                    bomAndBillExportVO.setType(vo.getType());
                    bomAndBillExportVO.setBomId(vo.getBomId());
                    bomAndBillExportVOList.add(bomAndBillExportVO);

                    // 如果bomUsedTimesMap bom使用次数 中不含当前bom则加入map
                    if (!bomUsedTimesMap.containsKey(vo.getBomId())) {
                        bomUsedTimesMap.put(vo.getBomId(), 1);
                    } else { // 如果存在map中则使用次数加1
                        bomUsedTimesMap.put(vo.getBomId(), bomUsedTimesMap.get(vo.getBomId()) + 1);
                    }

                    // 如果bomModuleMap bom配置 中不含当前bom则加入map
                    if (!bomModuleMap.containsKey(vo.getBomId())) {
                        List<BomModuleExportVO> bomModuleExportVOS = new ArrayList<>();

                        for (BomDetailVO detailVO : vo.getBomDetailVOS()) {
                            BomModuleExportVO bomModuleExportVO = new BomModuleExportVO();
                            bomModuleExportVO.setPartNo(detailVO.getPartNo());
                            bomModuleExportVO.setBomId(detailVO.getBomId());
                            bomModuleExportVO.setType(detailVO.getType());
                            bomModuleExportVO.setCreateTime(detailVO.getCreateTime());
                            bomModuleExportVOS.add(bomModuleExportVO);
                        }
                        bomModuleMap.put(vo.getBomId(), bomModuleExportVOS);
                    }
                }
            }
        }

        DataVO dataVO = new DataVO();
        //
        List<BomUsedTimesExportVO> bomUsedTimesExportVOList = new ArrayList<>(); // bom使用次数
        for (String key : bomUsedTimesMap.keySet()) {
            BomUsedTimesExportVO exportVO = new BomUsedTimesExportVO();
            exportVO.setBomId(key);
            exportVO.setUsedTimes(bomUsedTimesMap.get(key));
            exportVO.setCreateTime(bomModuleMap.get(key).size() == 0 ? "" : bomModuleMap.get(key).get(0).getCreateTime());
            bomUsedTimesExportVOList.add(exportVO);
        }
        dataVO.setBomUsedTimesExportVOS(bomUsedTimesExportVOList);
        //
        List<BomModuleExportVO> bomModuleExportVOS = new ArrayList();
        for (String key : bomModuleMap.keySet()) {
            bomModuleExportVOS.addAll(bomModuleMap.get(key));
        }
        dataVO.setBomModuleExportVOS(bomModuleExportVOS);
        //
        dataVO.setBomAndBillExportVOS(bomAndBillExportVOList);

        log.info("统计完成");
        return dataVO;
    }

    public DataVO doIt1() {
        try {
            Map<String, List<BomModuleExportVO>> bomModuleMap = new HashMap<>(); // bom模块号配置
            Map<String, Integer> bomUsedTimesMap = new HashMap<>(); // bom使用次数map
            List<BomAndBillExportVO> bomAndBillExportVOList = new ArrayList<>(); // bom订单信息

            // 第一步，读取共享盘文件
            SMBClient client = new SMBClient();
            Connection connection = client.connect(smbAutoProperties.getHostname());
            AuthenticationContext ac = new AuthenticationContext(smbAutoProperties.getUsername(), smbAutoProperties.getPassword().toCharArray(), null);
            Session session = connection.authenticate(ac);
            DiskShare share = (DiskShare) session.connectShare(smbAutoProperties.getRootFolder());
            log.info(share.toString());
            List<FileIdBothDirectoryInformation> list = share.list("");
            int count = 0;
            for (FileIdBothDirectoryInformation item : list) { // 循环读取全部文件
                log.info("正在执行第" + (count++) + "个压缩包，" + item.getFileName());
                if (item.getFileName().startsWith("ARCHIVE_CHO_K426CSCQ") && item.getFileName().endsWith(".zip")) {
                    // 读取文件
                    com.hierynomus.smbj.share.File fileRead = share.openFile(
                            item.getFileName(),
                            EnumSet.of(AccessMask.GENERIC_READ),
                            null,
                            SMB2ShareAccess.ALL,
                            SMB2CreateDisposition.FILE_OPEN,
                            null);

                    InputStream is = fileRead.getInputStream();
                    File file = File.createTempFile("temp", ".zip");
                    FileUtils.copyInputStreamToFile(is, file);

                    // 第二步，解压zip，获取压缩包下的所有xml文件字符串， 一个压缩包文件返回多个xml文件
                    File srcFile = new File(file.getAbsolutePath());
                    ZipFile zipFile = null;

                    try {
                        zipFile = new ZipFile(srcFile, Charset.forName("GBK"));
                        Enumeration<?> entries = zipFile.entries();
                        while (entries.hasMoreElements()) { // 逐个解析压缩包下的xml文件
                            ZipEntry entry = (ZipEntry) entries.nextElement();
                            log.info(entry.getName());

                            if (entry.getName().endsWith(".xml")) {
                                StringBuilder xmlContentSb = new StringBuilder(); // xml文件的内容
                                try (InputStream in = zipFile.getInputStream(entry);
                                    InputStreamReader inputStreamReader = new InputStreamReader(in);
                                    BufferedReader reader = new BufferedReader(inputStreamReader)) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        xmlContentSb.append(line).append("\n");
                                    }

                                    // 第三步，dom解析xml
                                    List<BomAndBillDetailVO> bomAndBillDetailVOS = DomParsingUtil.parse(xmlContentSb.toString()); // 一个xml解析多个工单bom
                                    for (BomAndBillDetailVO vo : bomAndBillDetailVOS) {
                                        BomAndBillExportVO bomAndBillExportVO = new BomAndBillExportVO();
                                        bomAndBillExportVO.setModel(vo.getModel());
                                        bomAndBillExportVO.setOrder(vo.getOrder());
                                        bomAndBillExportVO.setCreateTime(vo.getCreateTime());
                                        bomAndBillExportVO.setType(vo.getType());
                                        bomAndBillExportVO.setBomId(vo.getBomId());
                                        bomAndBillExportVOList.add(bomAndBillExportVO);

                                        // 如果bomUsedTimesMap bom使用次数 中不含当前bom则加入map
                                        if (!bomUsedTimesMap.containsKey(vo.getBomId())) {
                                            bomUsedTimesMap.put(vo.getBomId(), 1);
                                        } else { // 如果存在map中则使用次数加1
                                            bomUsedTimesMap.put(vo.getBomId(), bomUsedTimesMap.get(vo.getBomId()) + 1);
                                        }

                                        // 如果bomModuleMap bom配置 中不含当前bom则加入map
                                        if (!bomModuleMap.containsKey(vo.getBomId())) {
                                            List<BomModuleExportVO> bomModuleExportVOS = new ArrayList<>();

                                            for (BomDetailVO detailVO : vo.getBomDetailVOS()) {
                                                BomModuleExportVO bomModuleExportVO = new BomModuleExportVO();
                                                bomModuleExportVO.setPartNo(detailVO.getPartNo());
                                                bomModuleExportVO.setBomId(detailVO.getBomId());
                                                bomModuleExportVO.setType(detailVO.getType());
                                                bomModuleExportVO.setCreateTime(detailVO.getCreateTime());
                                                bomModuleExportVOS.add(bomModuleExportVO);
                                            }
                                            bomModuleMap.put(vo.getBomId(), bomModuleExportVOS);
                                        }
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("unzip error from UnZipUtil." + srcFile.getName() + ".\n" + e);
                        throw new RuntimeException("unzip error from UnZipUtil", e);
                    } finally {
                        if (zipFile != null) {
                            try {
                                zipFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

            DataVO dataVO = new DataVO();
            //
            List<BomUsedTimesExportVO> bomUsedTimesExportVOList = new ArrayList<>(); // bom使用次数
            for (String key : bomUsedTimesMap.keySet()) {
                BomUsedTimesExportVO exportVO = new BomUsedTimesExportVO();
                exportVO.setBomId(key);
                exportVO.setUsedTimes(bomUsedTimesMap.get(key));
                exportVO.setCreateTime(bomModuleMap.get(key).size() == 0 ? "" : bomModuleMap.get(key).get(0).getCreateTime());
                bomUsedTimesExportVOList.add(exportVO);
            }
            dataVO.setBomUsedTimesExportVOS(bomUsedTimesExportVOList);
            //
            List<BomModuleExportVO> bomModuleExportVOS = new ArrayList();
            for (String key : bomModuleMap.keySet()) {
                bomModuleExportVOS.addAll(bomModuleMap.get(key));
            }
            dataVO.setBomModuleExportVOS(bomModuleExportVOS);
            //
            dataVO.setBomAndBillExportVOS(bomAndBillExportVOList);

            log.info("统计完成");
            return dataVO;

        } catch (Exception e) {
            log.error("执行失败");
            e.printStackTrace();
            return null;
        }
    }
}
