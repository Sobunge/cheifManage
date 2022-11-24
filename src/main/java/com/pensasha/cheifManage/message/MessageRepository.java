package com.pensasha.cheifManage.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{
 
    public List<Message> findAllBySenderIdNumber(int idNumber);
    public List<Message> findAllByRecieversIdNumber(int idNumber);
    public List<Message> findAllByRecieversIdNumberAndStatus(int idNumber, Status status);
}
