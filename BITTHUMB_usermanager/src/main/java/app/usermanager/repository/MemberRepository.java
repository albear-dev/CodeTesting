package app.usermanager.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import app.usermanager.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
	
	/**
	 * @param userId
	 * @return Boolean
	 * 
	 * 아이디가 등록되어 있는지 확인
	 */
	@Query(value="select CAST(count(1) AS boolean) from MEMBER where USER_ID = :userId", nativeQuery=true)
	Boolean existsById(@Param("userId") String userId);

	/**
	 * @param userId
	 * @return MemberEntity
	 * 
	 * 아이디 정보로 회원 정보를 조회
	 */
	@Query(value="select * from MEMBER where USER_ID = :userId", nativeQuery=true)
	MemberEntity findByUserId(@Param("userId") String userId);
	
	/**
	 * @param userId
	 * @param lastLoginDate
	 * 
	 * 로그인시 마지막 로그인 시간 업데이트시 사용
	 */
	@Transactional
	@Modifying
	@Query(value="UPDATE MEMBER set LAST_LOGIN_DATE = :lastLoginDate where USER_ID = :userId", nativeQuery=true)
	void updateLastLoginDate(@Param("userId") String userId, @Param("lastLoginDate") Date lastLoginDate);
}
