package com.omenaapp.auth_service.domain;

import java.util.UUID;

public class Role {

    private final UUID id;
    private final String name;

//  choix entre seulement admin et non admin 
    public Role(UUID id, String name) {
        this.id = id;
        this.name = name;
        }

    public UUID id() { return id; }
    public String name() { return name; }



}
