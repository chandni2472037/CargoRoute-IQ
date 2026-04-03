package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entities.DriverAck;

@Repository
public interface DriverAckRepository extends JpaRepository<DriverAck, Long> {

    //JPA relationship-based queries
    DriverAck findByDispatch_DispatchID(Long dispatchID);

    DriverAck findByDriver_DriverID(Long driverID);
}
