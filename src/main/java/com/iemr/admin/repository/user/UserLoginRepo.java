package com.iemr.admin.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.user.M_User;

@Repository
public interface UserLoginRepo extends JpaRepository<M_User, Integer> {

	public M_User findByUserID(Integer userID);

}
