package icu.lowcoder.spring.cloud.authentication.entity.superclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter @Setter
@MappedSuperclass
public class UUIDIdentifierEntity {

    @Id
    @GeneratedValue(generator = "uuidIdGenerator")
    @GenericGenerator(name = "uuidIdGenerator", strategy = "uuid2")
    private UUID id;

}
