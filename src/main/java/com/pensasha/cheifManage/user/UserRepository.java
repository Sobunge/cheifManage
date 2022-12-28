package com.pensasha.cheifManage.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    

    public List<User> findAllByTitle(Title title);

    public Boolean existsByOffice(Office office);

    public List<User> findAllByStatus(Status status);
}
