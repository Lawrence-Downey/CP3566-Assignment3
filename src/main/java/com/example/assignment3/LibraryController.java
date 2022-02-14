package com.example.assignment3;



import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/library")
public class LibraryController {

    @GET
    @Produces("text/plain")
    public String greetings(){
        return "Greetings! " +
                "\nIn this API you will find have access to all of the books in the library. " +
                "\nIf you do not think you can read them all, you also have the option to select a single book title." +
                "\nNot interested in titles? That's fine too. You can also view all of the authors. " +
                "\nHave a favorite author? Great! Search for them and cross your fingers they are among the ones we carry " +
                "\nbooks for. " +
                "\n " +
                "\nWrote your own book? Fantastic!! With out newest update, you can now upload your very own book! " +
                "\nNothing screams success at a high school reunion like having your book published and on display in " +
                "\nthe local library! " +
                "\nHaven't written a book yet but consider yourself an author? Don't sweat it! We also have a space for " +
                "\nyou to add your name to our database. " +
                "\nSuddenly remember helping a colleague write a book but when you request to view it you see they failed " +
                "\n to include your name? Lucky for you, we also have a feature that will allow you to associate yourself " +
                "\n with a book of your choice. Bring on the royalties and back pay! " +
                "\n " +
                "\nWe also allow modifications to take place on our Books and Authors (Good chance to get back at your " +
                "\n collegue!) " +
                "\nAnd finally, we have the DELETE section. If you have to continue reading this to find out what this " +
                "\nsection does, then perhaps we shouldn't have let you onto our webapp....";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/books")
    public List<Book> viewBooks() {
        String booksQuery = "SELECT * FROM titles;";
        List<Book> bookList =  new ArrayList<>();
        try(Connection conn = DBConnection.initDatabase();
            Statement stmt = conn.createStatement();
            ResultSet bookSet = stmt.executeQuery(booksQuery)) {
            while (bookSet.next()) {
                Book book = new Book(bookSet.getString("isbn"), bookSet.getString("title"),
                        bookSet.getInt("editionNumber"), bookSet.getString("copyright"));

                String getBooks = "SELECT * FROM titles t " +
                        "INNER JOIN authorisbn a On " +
                        "t.isbn = a.isbn INNER JOIN authors x on " +
                        "a.authorID = x.authorID " +
                        "WHERE t.isbn = ?;";
                PreparedStatement ps = conn.prepareStatement(getBooks);
                ps.setString(1, book.getIsbn());
                ResultSet authSet = ps.executeQuery();
                while (authSet.next()) {
                    Author author = new Author(authSet.getInt("authorID"), authSet.getString("firstName"),
                            authSet.getString("lastName"));
                    book.getAuthorList().add(author);
                }
                bookList.add(book);
            }
            return bookList;
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/authors")
    public List<Author> viewAuthors(){
        String authQuery = "SELECT * FROM authors;";
        List<Author> authList =  new ArrayList<>();
        try(Connection conn = DBConnection.initDatabase();
            Statement stmt = conn.createStatement();
            ResultSet authSet = stmt.executeQuery(authQuery)) {
            while (authSet.next()) {

                Author author = new Author(authSet.getInt("authorID"),
                        authSet.getString("firstName"),
                        authSet.getString("lastName"));

                String getAuthors = "SELECT * FROM titles t " +
                        "INNER JOIN authorisbn a On " +
                        "t.isbn = a.isbn INNER JOIN authors x on " +
                        "a.authorID = x.authorID " +
                        "WHERE x.authorID = ?;";
                PreparedStatement ps = conn.prepareStatement(getAuthors);
                ps.setInt(1, author.getAuthorID());
                ResultSet rsB = ps.executeQuery();
                while (rsB.next()) {
                    Book book = new Book(rsB.getString("isbn"), rsB.getString("title"),
                            rsB.getInt("editionNumber"), rsB.getString("copyright"));
                    author.getBookList().add(book);
                }
                authList.add(author);
            }
            return authList;
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/book/{id}")
    public Book oneBook(@PathParam("id") String id) throws SQLException, ClassNotFoundException {
        String query = "SELECT * from titles " +
                        "WHERE isbn = ?";

        try (Connection conn = DBConnection.initDatabase()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Book book = new Book(rs.getString("isbn"), rs.getString("title"),
                        rs.getInt("editionNumber"), rs.getString("copyright"));

                String getBooks = "SELECT * FROM titles t " +
                        "INNER JOIN authorisbn a On " +
                        "t.isbn = a.isbn INNER JOIN authors x on " +
                        "a.authorID = x.authorID " +
                        "WHERE t.isbn = ?;";
                PreparedStatement presta = conn.prepareStatement(getBooks);
                presta.setString(1, book.getIsbn());
                ResultSet authSet = presta.executeQuery();
                while (authSet.next()) {
                    Author author = new Author(authSet.getInt("authorID"), authSet.getString("firstName"),
                            authSet.getString("lastName"));
                    book.getAuthorList().add(author);
                }
                return book;
            }else{
                return null;
            }
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/author/{id}")
    public Author oneAuthor(@PathParam("id") String id) throws SQLException, ClassNotFoundException {
        String query = "SELECT * from authors " +
                        "WHERE authorID = ?;";

        try (Connection conn = DBConnection.initDatabase()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Author author = new Author(rs.getInt("authorID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"));

                String getAuthors = "SELECT * FROM titles t " +
                        "INNER JOIN authorisbn a On " +
                        "t.isbn = a.isbn INNER JOIN authors x on " +
                        "a.authorID = x.authorID " +
                        "WHERE x.authorID = ?;";
                PreparedStatement presta = conn.prepareStatement(getAuthors);
                presta.setInt(1, author.getAuthorID());
                ResultSet rsB = presta.executeQuery();
                while (rsB.next()) {
                    Book book = new Book(rsB.getString("isbn"), rsB.getString("title"),
                            rsB.getInt("editionNumber"), rsB.getString("copyright"));
                    author.getBookList().add(book);
                }
                return author;
            }else{
                return null;
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addbook")
    public Book addBook(@QueryParam("isbn") String isbn,
                        @QueryParam("title") String title,
                        @QueryParam("editionNumber") int editionNumber,
                        @QueryParam("copyright") String copyright) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO titles " +
                        "(isbn,title,editionNumber,copyright) " +
                        "VALUES(?, ?, ?, ?);";

        try(Connection conn = DBConnection.initDatabase()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, isbn);
            ps.setString(2, title);
            ps.setInt(3, editionNumber);
            ps.setString(4, copyright);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Book book = new Book(isbn, title, editionNumber, copyright);
                return book;
            }else{
                return null;
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addauthor")
    public Author addAuthor(@QueryParam("firstName") String firstName,
                            @QueryParam("lastName") String lastName) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO authors" +
                "(firstName, lastName) " +
                "VALUES(?, ?);";

        try(Connection conn = DBConnection.initDatabase()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, firstName);
            ps.setString(2, lastName);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String getID = "Select authorID FROM authors " +
                        "WHERE firstName = ? AND lastName = ?;";
                PreparedStatement presta = conn.prepareStatement(getID);
                presta.setString(1, firstName);
                presta.setString(2, lastName);
                ResultSet idSet = presta.executeQuery();
                Author author = null;
                while (idSet.next()) {
                    author = new Author(idSet.getInt("authorID"), firstName, lastName);
                }
                return author;
            }else{
                return null;
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/associateAuthor")
    public Author associateAuthor(@QueryParam("authorID") Integer authorID,
                                  @QueryParam("isbn") String isbn) throws SQLException, ClassNotFoundException{
        String query = "INSERT INTO authorisbn " +
                        "(authorID, isbn) " +
                        "VALUES(?, ?);";

        try(Connection conn = DBConnection.initDatabase()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, authorID);
            ps.setString(2, isbn);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String getAuthor = "SELECT firstName, lastName FROM authors " +
                        "WHERE authorID = ?;";

                String getBook = "SELECT title, editionNumber, copyright FROM titles " +
                        "WHERE isbn = ?;";

                PreparedStatement psa = conn.prepareStatement(getAuthor);
                psa.setInt(1, authorID);
                ResultSet rsa = psa.executeQuery();

                PreparedStatement psb = conn.prepareStatement(getBook);
                psb.setString(1, isbn);
                ResultSet rsb = psb.executeQuery();
            }
        }
    }

    @DELETE
    @Produces("text/plain")
    @Path("/delbook/{id}")
    public String deleteBook(@PathParam("id") String id) throws SQLException, ClassNotFoundException{
        String query = "DELETE FROM titles " +
                        "WHERE isbn = ?;";

        try(Connection conn = DBConnection.initDatabase()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            return "Attempt to delete book: " + rs.getString("title") + " was successful!";
        }
    }

    @DELETE
    @Produces("text/plain")
    @Path("/delauthor/{id}")
    public String deleteAuthor(@PathParam("id") Integer id) throws SQLException, ClassNotFoundException{
        String query = "DELETE FROM authors " +
                "WHERE authorID = ?;";

        try(Connection conn = DBConnection.initDatabase()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            return "Attempt to delete Author: " + rs.getString("firstName") + " " +
                    rs.getString("lastName") + " was successful!";
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/modbook")
    public Book modBook(@QueryParam("isbn") String isbn,
                        @QueryParam("title") String title,
                        @QueryParam("editionNumber") Integer editionNumber,
                        @QueryParam("copyright") String copyright) throws SQLException, ClassNotFoundException {

        String query1 = "UPDATE titles " +
                        "SET title = ? " +
                        "WHERE isbn = ?;";
        String query2 = "UPDATE titles " +
                        "SET editionNumber = ? " +
                        "WHERE isbn = ?;";
        String query3 = "UPDATE titles " +
                        "SET copyright = ? " +
                        "WHERE isbn = ?;";

        try(Connection conn = DBConnection.initDatabase()) {

            if (isbn != null) {
                if(title != null) {
                    PreparedStatement ps1 = conn.prepareStatement((query1));
                    ps1.setString(1, title);
                    ps1.setString(2, isbn);
                    ps1.executeUpdate();
                }
                if(editionNumber != null) {
                    PreparedStatement ps2 = conn.prepareStatement((query2));
                    ps2.setInt(1, editionNumber);
                    ps2.setString(2, isbn);
                    ps2.executeUpdate();
                }
                if(copyright != null) {
                    PreparedStatement ps3 = conn.prepareStatement((query3));
                    ps3.setString(1, copyright);
                    ps3.setString(2, isbn);
                    ps3.executeUpdate();
                }
                //If partnered with a form, these fields must be filled in with either
                //new information or existing information.
                return new Book(isbn, title, editionNumber, copyright);
            }else{
                return null;
            }
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/modauthor")
    public Author modAuthor(@QueryParam("authorID") Integer authorID,
                            @QueryParam("firstName") String firstName,
                            @QueryParam("lastName") String lastName) throws SQLException, ClassNotFoundException {

        String query1 = "UPDATE authors " +
                "SET firstName = ? " +
                "WHERE authorID = ?;";
        String query2 = "UPDATE authors " +
                "SET lastName = ? " +
                "WHERE authorID = ?;";

        try(Connection conn = DBConnection.initDatabase()) {

            if (authorID != null) {
                if(firstName != null) {
                    PreparedStatement ps1 = conn.prepareStatement((query1));
                    ps1.setString(1, firstName);
                    ps1.setInt(2, authorID);
                    ps1.executeUpdate();
                }
                if(lastName != null) {
                    PreparedStatement ps2 = conn.prepareStatement((query2));
                    ps2.setString(1, lastName);
                    ps2.setInt(2, authorID);
                    ps2.executeUpdate();
                }

                //If partnered with a form, these fields must be filled in with either
                //new information or existing information.
                return new Author(authorID, firstName, lastName);
            }else{
                return null;
            }
        }
    }
}
