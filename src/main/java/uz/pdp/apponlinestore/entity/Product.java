package uz.pdp.apponlinestore.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.apponlinestore.entity.template.AbsLongEntity;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(
//        name = "product"
)
public class Product extends AbsLongEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    private Attachment photo;

    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product obj = (Product) o;
        return Objects.equals(getId(), obj.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
