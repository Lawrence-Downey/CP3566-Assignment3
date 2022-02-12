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
                        "WHERE authorID = ?";

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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addbook")
    public String addBook(@FormParam("isbn") String isbn,
                        @FormParam("title") String title,
                        @FormParam("editionNumber") int editionNumber,
                        @FormParam("copyright") String copyright) throws SQLException, ClassNotFoundException {
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
                return "Your book: " + book.getTitle() + " has been successfully added to our Library!";
            }else{
                return null;
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addauthor")
    public String addAuthor(@FormParam("firstName") String firstName,
                            @FormParam("lastName") String lastName) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO authors" +
                "(firstName, lastName) " +
                "VALUES(?, ?);";

        try(Connection conn = DBConnection.initDatabase()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, firstName);
            ps.setString(2, lastName);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Author author = new Author(author.getAuthorID(), firstName, lastName);
                return "Congratulations! " +
                        "\n " + firstName + " " + lastName + " has successfully been added to our Authors list!";
            }else{
                return null;
            }
        }
    }
}
