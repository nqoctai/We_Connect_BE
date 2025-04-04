package mobile.doan.supertodolist.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

    private String avatar;

    private boolean isActive;

    private String codeId;

    private Instant codeExpired;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        // this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        // ? SecurityUtil.getCurrentUserLogin().get()
        // : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        // this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        // ? SecurityUtil.getCurrentUserLogin().get()
        // : "";

        this.updatedAt = Instant.now();
    }
}
