package com.example.model;/**
 * Created by hanqf on 2020/3/13 14:56.
 */


import javax.persistence.*;
import java.util.Set;

/**
 * @author hanqf
 * @date 2020/3/13 14:56
 */
@Entity(name = "role")
@Table(name = "role")
public class Role {
    //主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;

    //FetchType.LAZY，一定要开启事务，否则会报no session错误，@Transactional
    @ManyToMany(mappedBy="roles",cascade={CascadeType.DETACH},fetch=FetchType.LAZY)
    private Set<User> users;

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
