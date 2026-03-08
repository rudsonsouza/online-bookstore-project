package com.bookstore.rest;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;
import com.bookstore.service.BookService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    private BookService bookService;

    @POST
    public Response createBook(Book book) {
        try {
            Book created = bookService.createBook(book);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response getAllBooks(@QueryParam("category") String category) {
        List<Book> books;
        if (category != null && !category.isEmpty()) {
            try {
                BookCategory bookCategory = BookCategory.valueOf(category.toUpperCase());
                books = bookService.getBooksByCategory(bookCategory);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid category: " + category).build();
            }
        } else {
            books = bookService.getAllBooks();
        }
        return Response.ok(books).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id) {
        try {
            Book book = bookService.getBookById(id);
            return Response.ok(book).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/isbn/{isbn}")
    public Response getBookByIsbn(@PathParam("isbn") String isbn) {
        try {
            Book book = bookService.getBookByIsbn(isbn);
            return Response.ok(book).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") Long id, Book book) {
        try {
            Book updated = bookService.updateBook(id, book);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Long id) {
        try {
            bookService.deleteBook(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
