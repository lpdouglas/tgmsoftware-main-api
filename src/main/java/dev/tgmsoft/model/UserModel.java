package dev.tgmsoft.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserModel {

    @Id
    private ObjectId id;
    private String name;
    private String email;
    private String password;
}
