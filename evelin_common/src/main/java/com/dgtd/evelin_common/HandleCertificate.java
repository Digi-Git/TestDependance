package com.dgtd.evelin_common;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HandleCertificate {
    @Transactional( propagation = Propagation.REQUIRES_NEW)
    List<String> generateECW() throws Exception;
}
