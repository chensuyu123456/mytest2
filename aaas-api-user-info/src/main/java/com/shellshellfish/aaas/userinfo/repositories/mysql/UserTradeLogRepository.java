package com.shellshellfish.aaas.userinfo.repositories.mysql;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shellshellfish.aaas.userinfo.model.dao.UiPortfolio;
import com.shellshellfish.aaas.userinfo.model.dao.UiTrdLog;

public interface UserTradeLogRepository extends PagingAndSortingRepository<UiTrdLog, Long> {

  Page<UiTrdLog> findByUserId(Pageable pageable,Long userId);
  
  List<UiTrdLog> findByUserId(Long userId);
}
