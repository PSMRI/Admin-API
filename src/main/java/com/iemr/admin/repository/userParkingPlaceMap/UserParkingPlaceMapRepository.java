/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.admin.repository.userParkingPlaceMap;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.userParkingPlaceMap.M_UserParkingPlaceMap;

@Repository
@RestResource(exported = false)
public interface UserParkingPlaceMapRepository extends CrudRepository<M_UserParkingPlaceMap, Integer>{
	
/*	@Query("SELECT u.userParkingPlaceMapID, u.userID,usr.userName,usr.genderID,usr.emergencyContactNo, usr.aadhaarNo, s.stateID, s.stateName, d.districtID,"
			+ " d.districtName, u.parkingPlaceID, pp.parkingPlaceName, "
			+ "u.providerServiceMapID, u.deleted"
			+ " FROM M_UserParkingPlaceMap u "
			+ " LEFT JOIN u.m_user usr "
			+ " LEFT JOIN u.m_parkingplace pp "
			+ " LEFT JOIN u.m_providerServiceMapping p "
			+ " LEFT JOIN p.state s"
			+ " LEFT JOIN p.m_district d"
			+ " where (p.stateID IS NULL or cast(p.stateID as string) like :stateID) "
			+ " AND (p.districtID IS NULL or cast(p.districtID as string) like :districtID) "
			+ " AND (u.parkingPlaceID IS NULL or cast(u.parkingPlaceID as string) like :parkingPlaceID)")
	List<Objects[]> getUserParkingPlaceMappings(@Param("stateID")String stateID, @Param("districtID")String districtID, 
			@Param("parkingPlaceID")String parkingPlaceID);
*/
	
	@Query(value = "SELECT upmap.UserParkingPlaceMapID,usr.UserID,usr.firstName,usr.lastName,usr.UserName,usr.DesignationID, group_concat(usro.RoleID), group_concat(r.RoleName),usr.GenderID,g.GenderName,psm.StateID,s.stateName,"
			+ " psam.DistrictID, d.districtName , upmap.ParkingPlaceID, pp.ParkingPlaceName, usr.emergencyContactNo, psm.ProviderServiceMapID, upmap.deleted "
			+ " FROM m_userservicerolemapping usro"
			+ " RIGHT JOIN m_user usr ON usr.UserID=usro.UserID"
			+ " LEFT JOIN m_userdemographics usd ON usd.UserID=usr.UserID"
			+ " LEFT JOIN m_role r ON usro.RoleID = r.RoleID"
			+ " LEFT JOIN m_gender g ON usr.GenderID = g.GenderID"
			+ " LEFT JOIN m_userparkingplacemap upmap ON upmap.UserID = usr.UserID"
			+ " LEFT JOIN m_parkingplace pp ON upmap.ParkingPlaceID = pp.ParkingPlaceID"
			+ " LEFT JOIN m_providerservicemapping psm ON usro.ProviderServiceMapID = psm.ProviderServiceMapID"
			+ " LEFT JOIN m_state s ON psm.stateID=s.stateID"
			+ " LEFT JOIN m_providerserviceaddmapping psam ON psm.ProviderServiceMapID=psam.ProviderServiceMapID"
			+ " LEFT JOIN m_district d ON psam.districtID = d.districtID"
			+ " where (psm.stateID IS NULL or cast(psm.stateID as char) like :stateID) "
			+ " AND (psam.districtID IS NULL or cast(psam.districtID as char) like :districtID)"
			+ " AND (upmap.parkingPlaceID IS NULL or cast(upmap.parkingPlaceID as char) like :parkingPlaceID)"
			+ " AND psm.serviceProviderID =:serviceProviderID"
			+ " AND (usr.designationID IS NULL or cast(usr.designationID as char) like :designationID) group by usr.UserID", nativeQuery =true)
	List<Objects[]> getUserParkingPlaceMappings(@Param("serviceProviderID")Integer serviceProviderID, @Param("stateID")String stateID, @Param("districtID")String districtID, 
			@Param("parkingPlaceID")String parkingPlaceID, @Param("designationID")String designationID);

	
	@Transactional
	@Modifying
	@Query("update M_UserParkingPlaceMap u set u.deleted=:deleted, u.modifiedBy=:modifiedBy where u.userParkingPlaceMapID =:userParkingPlaceMapID")
	int updateUserParkingPlaceMapStatus(@Param("userParkingPlaceMapID")Integer userParkingPlaceMapID, @Param("deleted")Boolean deleted, @Param("modifiedBy")String modifiedBy);
	
	@Query("SELECT v FROM M_UserParkingPlaceMap v where v.userParkingPlaceMapID =:userParkingPlaceMapID")
	M_UserParkingPlaceMap getUserParkingPlaceMapByID(@Param("userParkingPlaceMapID") Integer userParkingPlaceMapID);


	
//	@Query("SELECT m_uppm.userParkingPlaceMapID,usr.userID,usr.firstName,usr.lastName,usr.userName,usr.designationID,"
//			+ " d.districtID, pp.parkingPlaceID, pp.parkingPlaceName,m_uppm.providerServiceMapID,m_uppm.deleted,"
//			+ " usr.deleted as userDeleted "
//			+ " FROM M_UserParkingPlaceMap m_uppm"
//			+ " LEFT JOIN m_uppm.m_user usr"
//			+ " LEFT JOIN m_uppm.m_parkingplace pp"
//			+ " LEFT JOIN m_uppm.m_district d"
//			+ " where m_uppm.providerServiceMapID=:providerServiceMapID  AND pp.parkingPlaceID=:parkingPlaceID AND usr.designationID=:designationID"
//			+" group by usr.userID")
//	List<Objects[]> getUserParkingPlaceMappings1(@Param("providerServiceMapID")Integer providerServiceMapID,@Param("districtID") Integer districtID,
//			@Param("parkingPlaceID")Integer parkingPlaceID,@Param("designationID")Integer designationID);
	
	@Query("SELECT m_uppm.userParkingPlaceMapID,usr.userID,usr.firstName,usr.lastName,usr.userName,usr.designationID,"
			+ " d.districtID, pp.parkingPlaceID, pp.parkingPlaceName,m_uppm.providerServiceMapID,m_uppm.deleted,"
			+ " usr.deleted as userDeleted,desg.designationName "
			+ " FROM M_UserParkingPlaceMap m_uppm"
			+ " LEFT JOIN m_uppm.m_user usr"
			+ " LEFT JOIN m_uppm.m_parkingplace pp"
			+ " LEFT JOIN m_uppm.m_district d"
			+ " LEFT JOIN usr.m_designation desg"
			+ " where m_uppm.providerServiceMapID=:providerServiceMapID  AND pp.parkingPlaceID=:parkingPlaceID AND usr.designationID=:designationID")
	List<Objects[]> getUserParkingPlaceMappings1(@Param("providerServiceMapID")Integer providerServiceMapID,@Param("parkingPlaceID")Integer parkingPlaceID,@Param("designationID")Integer designationID);

@Query("select upp.userID from M_UserParkingPlaceMap upp left join upp.m_user u where u.designationID=:desgination and upp.providerServiceMapID=:providerServiceMapID and upp.deleted=false")
	List<Integer> getmappedids(@Param("providerServiceMapID")Integer providerServiceMapID, @Param("desgination")Integer designationID);


List<M_UserParkingPlaceMap> findByProviderServiceMapIDAndUserIDAndDeleted(Integer providerServiceMapID, Integer userID, boolean b);

}
