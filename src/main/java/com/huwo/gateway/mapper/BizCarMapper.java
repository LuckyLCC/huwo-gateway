package com.huwo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huwo.gateway.domain.dto.BizCarDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface BizCarMapper extends BaseMapper<BizCarDTO> {


    @Select(" SELECT car.ID             as id,\n" +
            "               car.No             as vehicleNo,\n" +
            "               car.DepartmentID   as departmentId,\n" +
            "               detail.PlateColor  as plateColor,\n" +
            "               detail.Brand       as brand,\n" +
            "               detail.Certificate as certificateNo\n" +
            "        FROM biz_car car\n" +
            "                 INNER JOIN biz_cardetail detail ON car.id = detail.carID\n" +
            "        WHERE car.ID = #{vehicleId}")
    BizCarDTO getBizCarById(Long id);
}
