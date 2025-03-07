package br.com.somestudy.data.dto.request;

import java.util.Objects;

public class EmailRequestDTO {

	private String to;
	private String from;
	private String body;
	
	public EmailRequestDTO() {}

	@Override
	public int hashCode() {
		return Objects.hash(body, from, to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailRequestDTO other = (EmailRequestDTO) obj;
		return Objects.equals(body, other.body) && Objects.equals(from, other.from) && Objects.equals(to, other.to);
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
