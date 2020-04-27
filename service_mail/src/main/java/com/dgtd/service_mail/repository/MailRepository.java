package com.dgtd.service_mail.repository;

import com.dgtd.service_mail.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MailRepository extends JpaRepository<Mail, Integer> {

    @Query(value = "select date_import from ecoles.mail where id =  (select max(id) from ecoles.mail)",nativeQuery = true)
    String getLastDateImport();


}
