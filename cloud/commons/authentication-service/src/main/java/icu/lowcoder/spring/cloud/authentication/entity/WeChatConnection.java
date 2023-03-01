package icu.lowcoder.spring.cloud.authentication.entity;

import icu.lowcoder.spring.cloud.authentication.dict.WeChatAppType;
import icu.lowcoder.spring.cloud.authentication.entity.superclass.AuditingUUIDIdentifierEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@SQLDelete(sql = "update we_chat_connection set deleted = true where id = ? ")
@Where(clause = "deleted = false")
public class WeChatConnection extends AuditingUUIDIdentifierEntity {

    @ManyToOne
    private Account account;

    @Column(nullable = false)
    private String appId;

    @Column(nullable = false)
    private String openId;

    private String nickname;

    private Integer gender;

    private String province;

    private String city;

    private String country;

    private String avatar;

    private String unionId;

    @Enumerated(EnumType.STRING)
    private WeChatAppType appType;

    private Boolean deleted = false;
}
