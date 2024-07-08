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
@Entity(name = "user_product")
public class UserProduct extends AbsLongEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProduct obj = (UserProduct) o;
        return Objects.equals(getId(), obj.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
