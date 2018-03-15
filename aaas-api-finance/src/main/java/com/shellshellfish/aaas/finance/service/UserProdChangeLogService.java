package com.shellshellfish.aaas.finance.service;

import com.shellshellfish.aaas.finance.model.dto.UserProdChg;
import com.shellshellfish.aaas.finance.model.dto.UserProdChgDetail;
import java.util.List;

/**
 * Created by chenwei on 2018- 三月 - 15
 */

public interface UserProdChangeLogService {

  List<UserProdChg> getGeneralChangeLogs(Long prodId);

  List<UserProdChgDetail> getDegailChangeLogs(Long prodId, Long changeSeq);
}
