package br.com.somestudy.integrationtests.dto.wrappers.xmlandyaml;

import java.util.List;

import br.com.somestudy.integrationtests.dto.PersonDTO;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelPerson {
	
	private static final long serialVersionUID=1L;
	
	public List<PersonDTO> content;
	
	public PagedModelPerson() {}
	
	public List<PersonDTO> getContent(){
		return content;
	}
	
	public void setContent(List<PersonDTO> content) {
		this.content=content;
	}
	

}
