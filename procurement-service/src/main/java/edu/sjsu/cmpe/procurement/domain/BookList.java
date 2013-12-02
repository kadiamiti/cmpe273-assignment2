package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookList {
	
	@JsonProperty
	private List<Book> shipped_books;

	public BookList()
	{
		shipped_books = new ArrayList<Book>();
	}
	/**
     * @return the book_list
     */
    public List<Book> getbooks() {
    	return shipped_books;
    }

    /**
     * @param book_list
     *            the isbn to set
     */
    public void setIsbn(List<Book> shipped_books) {
    	this.shipped_books = shipped_books;
    }
}
