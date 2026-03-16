package com.bookstore.rest;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;
import com.bookstore.service.BookService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

@Tag(name = "Books", description = "Book catalogue management")
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    private BookService bookService;

    @Operation(summary = "Create a new book")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Book created"),
            @APIResponse(responseCode = "400", description = "Invalid input or duplicate ISBN")
    })
    @POST
    public Response createBook(@Valid Book book) {
        Book created = bookService.createBook(book);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Operation(summary = "List all books", description = "Supports optional category filter and pagination")
    @APIResponse(responseCode = "200", description = "List of books")
    @GET
    public Response getAllBooks(
            @Parameter(description = "Filter by category (e.g. HORROR, SCIENCE_FICTION)")
            @QueryParam("category") String category,
            @Parameter(description = "Zero-based page number") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size (default 20)") @QueryParam("size") @DefaultValue("20") int size) {
        List<Book> books;
        if (category != null && !category.isEmpty()) {
            BookCategory bookCategory = BookCategory.valueOf(category.toUpperCase());
            books = bookService.getBooksByCategory(bookCategory, page, size);
        } else {
            books = bookService.getAllBooks(page, size);
        }
        return Response.ok(books).build();
    }

    @Operation(summary = "Get a book by ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Book found"),
            @APIResponse(responseCode = "404", description = "Book not found")
    })
    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id) {
        Book book = bookService.getBookById(id);
        return Response.ok(book).build();
    }

    @Operation(summary = "Get a book by ISBN")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Book found"),
            @APIResponse(responseCode = "404", description = "Book not found")
    })
    @GET
    @Path("/isbn/{isbn}")
    public Response getBookByIsbn(@PathParam("isbn") String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return Response.ok(book).build();
    }

    @Operation(summary = "Update a book")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Book updated"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Book not found")
    })
    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") Long id, @Valid Book book) {
        Book updated = bookService.updateBook(id, book);
        return Response.ok(updated).build();
    }

    @Operation(summary = "Delete a book")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Book deleted"),
            @APIResponse(responseCode = "404", description = "Book not found")
    })
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Long id) {
        bookService.deleteBook(id);
        return Response.noContent().build();
    }
}
