package com.dgtd.service_mail.service;

import com.dgtd.common.service.GenericService;
import com.dgtd.service_mail.entity.Mail;
import com.dgtd.service_mail.repository.MailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class MailService implements GenericService<Mail,Integer> {

    private final Logger log = LoggerFactory.getLogger(MailService.class);
    private MailRepository mailRepository;

    @Autowired
    public MailService(MailRepository mailRepository){
        this.mailRepository = mailRepository;
    }

    public String getLastDateImport(){
        return mailRepository.getLastDateImport();
    }

    @Override
    public Mail save(Mail entity) {
        return mailRepository.save(entity);
    }

    @Override
    public Optional<Mail> findOne(Integer id) {
        return Optional.empty();
    }

    @Override
    public Page<Mail> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Mail> findAll() {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }
}
