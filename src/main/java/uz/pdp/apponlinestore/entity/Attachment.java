package uz.pdp.apponlinestore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import uz.pdp.apponlinestore.entity.template.AbsLongEntity;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "attachment")
public class Attachment extends AbsLongEntity {

    @Column(nullable = false,name = "original_name")
    private String originalName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,name = "content_type")
    private String contentType;

    private long size;

    @Column(columnDefinition = "varchar(1000)")
    private String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment obj = (Attachment) o;
        return Objects.equals(getId(), obj.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
