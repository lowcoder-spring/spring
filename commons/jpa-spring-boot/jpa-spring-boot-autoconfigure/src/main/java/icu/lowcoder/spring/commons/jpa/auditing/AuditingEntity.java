package icu.lowcoder.spring.commons.jpa.auditing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditingEntity {
    @CreatedDate
    private LocalDateTime createdTime;
    @CreatedBy
    private String createdUser;
    @LastModifiedDate
    private LocalDateTime modifiedTime;
    @LastModifiedBy
    private String modifiedUser;
}
