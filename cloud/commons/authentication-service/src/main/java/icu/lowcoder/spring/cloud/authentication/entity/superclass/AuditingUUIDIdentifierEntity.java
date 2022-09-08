package icu.lowcoder.spring.cloud.authentication.entity.superclass;

import icu.lowcoder.spring.commons.jpa.auditing.AuditingEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class AuditingUUIDIdentifierEntity extends AuditingEntity {
    @Id
    @GeneratedValue(generator = "uuidIdGenerator")
    @GenericGenerator(name = "uuidIdGenerator", strategy = "uuid2")
    private UUID id;
}
