package com.autonomous.drone.persistance.postgreSql.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authentication")
public class TokenObject {
    @Id
    @Column(name = "user_id")
    private long userId;
    @Column(length = 500)
    private String token;

    public TokenObject() {
    }

    public TokenObject(long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
