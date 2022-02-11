package com.example.assignment3;

import java.util.*;

public class Author{

    private int authorID;
    private String firstName;
    private String lastName;
    private List<Book> bookList = new ArrayList<>();

    public int getAuthorID() {
        return authorID;
    }
    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public List<Book> getBookList() {
        return bookList;
    }
    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }


    public Author(int authorID, String firstName, String lastName){
        this.authorID = authorID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
