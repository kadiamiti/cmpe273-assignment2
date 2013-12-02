package edu.sjsu.cmpe.procurement.domain;

import java.net.URL;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {

	@JsonProperty
    @NotNull
    private long isbn;

	@JsonProperty
    @NotEmpty
    private String title;

	@JsonProperty
    @NotEmpty
    private String category;
    
	@JsonProperty
    @NotEmpty
    private URL coverimage;


    /**
     * @return the isbn
     */
    public long getIsbn() {
    	return isbn;
    }

    /**
     * @param isbn
     *            the isbn to set
     */
    public void setIsbn(long isbn) {
    	this.isbn = isbn;
    }

    /**
     * @return the title
     */
    public String getTitle() {
    	return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
    	this.title = title;
    }

    /**
     * @return the category
     */
    public String getCategory() {
    	return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
    	this.category = category;
    }

    /**
     * @return the coverimage
     */
    public URL getCoverimage() {
    	return coverimage;
    }

    /**
     * @param coverimage
     *            the coverimage to set
     */
    public void setCoverimage(URL coverImage) {
    	this.coverimage = coverImage;
    }

}

