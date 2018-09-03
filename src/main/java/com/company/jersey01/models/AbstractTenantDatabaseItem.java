package com.company.jersey01.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Abstract Database Item for all Tenant specific Tables. All DB Tables which have Tenant specific data should
 * extend this. Each row has required Tenant and TenantUser which created/updated this row.
 */
@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"tenant", "tenantUser"})
public abstract class AbstractTenantDatabaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    @NotNull
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

  /*
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id", updatable = false, insertable = false)
  @JsonIgnore
  private Tenant tenant;
  */

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_user_id", insertable = false, updatable = false)
    @JsonIgnore
    private TenantUser tenantUser;
  */
    @Column(name = "tenant_user_id")
    private Long tenantUserId;

    protected AbstractTenantDatabaseItem(Timestamp createdAt, Timestamp updatedAt, /*Tenant tenant,*/ String tenantId, /*TenantUser tenantUser,*/ Long tenantUserId) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        //this.tenant = tenant;
        this.tenantId = tenantId;
        //this.tenantUser = tenantUser;
        this.tenantUserId = tenantUserId;
    }
}
