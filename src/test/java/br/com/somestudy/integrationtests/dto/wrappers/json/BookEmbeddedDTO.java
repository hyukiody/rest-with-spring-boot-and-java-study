package br.com.somestudy.integrationtests.dto.wrappers.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.somestudy.integrationtests.dto.BookDTO;

public class BookEmbeddedDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("books")
	private List<BookDTO> books;
	
	public BookEmbeddedDTO() {};
	
	public List<BookDTO> getBooks(){
		return books;
	}
	
	public void setBooks(List<BookDTO> books) {
		this.books = books;
	}
	
}
