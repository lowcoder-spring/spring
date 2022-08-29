package icu.lowcoder.spring.commons.feign.page;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class FeignPage<T> implements Iterable<T>, Serializable {
    private List<T> content = new ArrayList<>();
    private boolean last;
    private int totalPages;
    private int totalElements;
    private int numberOfElements;
    private int size;
    private int number;

    public Pageable getPageable() {
        return PageRequest.of(number, size);
    }

    public boolean hasNext() {
        return this.getNumber() + 1 < this.getTotalPages();
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}
