package uz.pdp.apponlinestore.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.apponlinestore.entity.template.AbsLongEntity;
import uz.pdp.apponlinestore.enums.OrderStatusEnum;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "orders")
public class Order extends AbsLongEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    private double price;

    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order obj = (Order) o;
        return Objects.equals(getId(), obj.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
