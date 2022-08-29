package icu.lowcoder.spring.commons.jpa;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Setter
@Getter
@MappedSuperclass
@GenericGenerator(name = "idGenerator", strategy = "uuid2")
public class CommonEntity {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private UUID id;
}
