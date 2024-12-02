package com.iemr.admin.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.user.M_User;


@Repository
public interface UserLoginRepo extends CrudRepository<M_User, Long> {

	@Query(" SELECT u FROM M_User u WHERE u.userID = :userID AND u.Deleted = false ")
	public M_User getUserByUserID(@Param("userID") Long userID);

}
