package icu.lowcoder.spring.commons.ali.oss;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Policy {
    private String Version;
    private List<Statement> Statement = new ArrayList<>();

    @Getter
    @Setter
    public static class Statement {
        private String Effect;
        private String Action;
        private List<String> Resource;
    }
}
