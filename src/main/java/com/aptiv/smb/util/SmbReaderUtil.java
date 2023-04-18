package com.aptiv.smb.util;

import com.aptiv.smb.config.SmbAutoProperties;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * smb协议读取共享盘文件
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmbReaderUtil {
    private final SmbAutoProperties smbAutoProperties;
    public List<String> smbReader() {
        List<String> filePaths = new ArrayList<>();
        try {
            SMBClient client = new SMBClient();
            Connection connection = client.connect(smbAutoProperties.getHostname());
            AuthenticationContext ac = new AuthenticationContext(smbAutoProperties.getUsername(), smbAutoProperties.getPassword().toCharArray(), null);
            Session session = connection.authenticate(ac);
            DiskShare share = (DiskShare) session.connectShare(smbAutoProperties.getRootFolder());
            log.info(share.toString());
            List<FileIdBothDirectoryInformation> list = share.list("");
            for (FileIdBothDirectoryInformation item : list) { // 循环读取全部文件
                if (item.getFileName().startsWith("ARCHIVE_CHO_K426CSCQ") && item.getFileName().endsWith(".zip")) {
                    log.info(item.getFileName());

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
                    filePaths.add(file.getAbsolutePath());
                }
                // 测试使用，只读取几个文件
//                if (filePaths.size() == 5) {
//                    break;
//                }
            }
            return filePaths;
        } catch (IOException e) {
            log.error("smbReader error from SmbReaderUtil." + e);
            throw new RuntimeException("smbReader error from SmbReaderUtil", e);
        }
    }
}
