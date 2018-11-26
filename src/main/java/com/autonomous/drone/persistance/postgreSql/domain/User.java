package com.autonomous.drone.persistance.postgreSql.domain;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    @NotNull
    private String username;

    @NotEmpty
    @NotNull
    private String password;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<Role> roles = new HashSet<>();

    @Transactional
    public String[] createRoleNameArray(){
        List<String> roleNameList = new ArrayList<>();
        roles.forEach(role ->
            roleNameList.add(role.getName().toString())
        );

        return roleNameList.toArray(new String[ roleNameList.size() ]);
    }

    @Transactional
    public String[] createPermissionNameArray(){
        List<String> permissionNameList = new ArrayList<>();
        roles.forEach(role -> {
            role.getPermissions()
                    .forEach(
                            permission -> permissionNameList.add(permission.getName().toString())
                            );
        });

        return permissionNameList.toArray(new String[ permissionNameList.size() ]);
    }

    public User() { }

    public User(@NotEmpty @NotNull String username, @NotEmpty @NotNull String password) {
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
