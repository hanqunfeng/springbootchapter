package org.example.model;/**
 * Created by hanqf on 2020/3/1 01:02.
 */


/**
 * @author hanqf
 * @date 2020/3/1 01:02
 */
public class User2 {

    private Long id;

    private String userName;

    private String note ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "User2{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
