package icu.lowcoder.spring.cloud.authentication.entity;

import icu.lowcoder.spring.cloud.authentication.entity.superclass.AuditingUUIDIdentifierEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
@SQLDelete(sql = "update account set deleted = true where id = ? ")
@Where(clause = "deleted = false")
public class Account extends AuditingUUIDIdentifierEntity {

    private String name;

    private String phone;

    private String email;

    private String password;

    private Date registerTime = new Date();

    private Boolean enabled = true;

    private String authorities; // 默认授权

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
    @Where(clause = "deleted = false")
    private List<WeChatConnection> weChatConnections = new ArrayList<>();

    private Boolean deleted = false;

}
