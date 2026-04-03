package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KPI; 

 
public interface KPIRepo extends JpaRepository<KPI, Long> { 
} 

 