package com.huwo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huwo.gateway.domain.dto.BizDriverDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: TODO
 * @Author: liuchang
 * @CreateTime: 2022-10-19  17:20
 */
@Mapper
public interface BizDriverMapper extends BaseMapper<BizDriverDTO> {

    @Select("SELECT driver.ID                     as id,\n" +
            "               driver.No                     as driverNo,\n" +
            "               driver.PhoneNo                as phoneNo,\n" +
            "               driver.DepartmentID           as operatorId,\n" +
            "               driver.DepartmentSubsidiaryID as departmentId,\n" +
            "               driver.RealName               as driverName,\n" +
            "               driver.car_id                 as vehicleId,\n" +
            "               driver.LicenseNo              as licenseNo,\n" +
            "               driver.CheckState             as checkState,\n" +
            "               driver.ApproveDate            as approveDate,\n" +
            "               detail.CertificateNo          as certificateNo\n" +
            "        FROM biz_driver driver\n" +
            "                 INNER JOIN biz_driverdetail detail\n" +
            "                            ON driver.id = detail.DriverID\n" +
            "        WHERE driver.ID = #{driverId}\n" +
            "        ORDER BY detail.updatetime DESC limit 1")
    BizDriverDTO getBizDriverById(Long id);


    @Select("SELECT driver.ID                     as id,\n" +
            "               driver.No                     as driverNo,\n" +
            "               driver.PhoneNo                as phoneNo,\n" +
            "               driver.DepartmentID           as operatorId,\n" +
            "               driver.DepartmentSubsidiaryID as departmentId,\n" +
            "               driver.RealName               as driverName,\n" +
            "               driver.car_id                 as vehicleId,\n" +
            "               driver.LicenseNo              as licenseNo,\n" +
            "               driver.CheckState             as checkState,\n" +
            "               driver.ApproveDate            as approveDate,\n" +
            "               detail.CertificateNo          as certificateNo\n" +
            "        FROM biz_driver driver\n" +
            "                 INNER JOIN biz_driverdetail detail\n" +
            "                            ON driver.Id = detail.DriverID\n" +
            "        WHERE driver.PhoneNo = #{driverPhone}\n" +
            "        ORDER BY detail.updateTime DESC limit 1")
    BizDriverDTO getByDriverPhone(String driverPhone);
}
