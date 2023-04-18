package com.aptiv.smb;

import com.aptiv.smb.util.SmbReaderUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mine")
@SpringBootTest
@RequiredArgsConstructor
class SmbApplicationTests {

    @Autowired
    private final SmbReaderUtil smbReaderUtil;

    @Test
    void init() {
        smbReaderUtil.smbReader();
    }

}
