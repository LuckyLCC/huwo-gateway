package com.huwo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huwo.gateway.domain.dto.SysDepartmentDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ZhangAihua
 * @date 2021/5/12 0012 16:08
 */
@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartmentDTO> {

//    SysDepartment getByDepartmentId(Long departmentId);

}
