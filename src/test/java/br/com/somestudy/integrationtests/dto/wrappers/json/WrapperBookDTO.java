package br.com.somestudy.integrationtests.dto.wrappers.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WrapperBookDTO implements Serializable{
	
	private static final long serialVersionUD = 1L;
	
	@JsonProperty("_embedded")
	private BookEmbeddedDTO embedded;
	
	public WrapperBookDTO() {}
	
	public BookEmbeddedDTO getEmbedded() {
		return embedded;
	}
	
	public void setEmbedded(BookEmbeddedDTO embedded) {
		this.embedded = embedded;
	}

}
