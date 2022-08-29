package icu.lowcoder.spring.cloud.authentication.entity;

import icu.lowcoder.spring.commons.jpa.auditing.AuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@SQLDelete(sql = "update account set deleted = true where id = ? ")
@Where(clause = "deleted = false")
public class Account extends AuditingEntity {

    private String name;

    private String phone;

    private String email;

    private String password;

    private Date registerTime = new Date();

    private Boolean enabled = true;

    private String authorities; // 默认授权

    private Boolean deleted = false;

}
