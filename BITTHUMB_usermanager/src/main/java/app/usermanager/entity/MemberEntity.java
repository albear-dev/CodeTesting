package app.usermanager.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "MEMBER")
@Entity
public class MemberEntity {
	@Id
	String userId;
	String userPw;
	String userName;
	Date regDate;
	Date lastLoginDate;
}
