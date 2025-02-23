package br.com.somestudy.data.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * For security purposes, the client and the API should not be able to 
 * communicate directly with each other; so the client and the API should 
 * communicate only through a common object, which is this current class.
 * 
 * The extension of RepresentationModel is a base class for enabling the HATEOAS
 * principles in the applications, it facilitates the creation of the REST Controller
 * methods that return classes that carry their specific hypermedia links 
 * with them, enabling the API to be defined over these links;
 * 
 * Furthermore these RepresentationModel classes are extended by EntityModel
 * 
 * ;
*/



@Relation(collectionRelation = "books")
public class BookDTO extends RepresentationModel<BookDTO> implements Serializable {

	private static final long serialVersionUID = 1L;


	private Long id;
	private String author;
	private Date launchDate;
	private Double price;
	private String title;
	
	public BookDTO() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getLaunchDate() {
		return launchDate;
	}

	public void setLaunchDate(Date launchDate) {
		this.launchDate = launchDate;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(author, id, launchDate, price, title);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookDTO other = (BookDTO) obj;
		return Objects.equals(author, other.author) && Objects.equals(id, other.id)
				&& Objects.equals(launchDate, other.launchDate) && Objects.equals(price, other.price)
				&& Objects.equals(title, other.title);
	}
}
