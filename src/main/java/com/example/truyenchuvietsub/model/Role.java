package com.example.truyenchuvietsub.model;

import com.example.truyenchuvietsub.model.enums.EnumRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

@Document(collection = "roles")
@Getter
@Setter
public class Role implements GrantedAuthority {
    @Id
    private String id;

    private EnumRole name;


    @Override
    public String getAuthority() {
        return name.name();
    }
}
