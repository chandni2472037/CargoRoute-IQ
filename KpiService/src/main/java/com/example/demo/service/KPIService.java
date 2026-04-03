package com.example.demo.service;

import java.util.List;


import com.example.demo.Dto.*;

public interface KPIService {
	KPIDTO save(KPIDTO kpi);
	List<KPIDTO> getAll();
	KPIDTO getById(Long id);
	void delete(Long id);
}
