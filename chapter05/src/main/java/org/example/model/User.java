package org.example.model;/**
 * Created by hanqf on 2020/3/5 10:44.
 */


/**
 * @author hanqf
 * @date 2020/3/5 10:44
 */
public class User {

    private Long id;
    private String name;
    private String note;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
