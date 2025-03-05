package br.com.somestudy.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {
	
	private String host;
	private int port;
	private String username;
	private String password;
	private String from;
	private boolean ssl;
	
	@Override
	public int hashCode() {
		return Objects.hash(from, host, password, port, ssl, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailConfig other = (EmailConfig) obj;
		return Objects.equals(from, other.from) && Objects.equals(host, other.host)
				&& Objects.equals(password, other.password) && port == other.port && ssl == other.ssl
				&& Objects.equals(username, other.username);
	}

	public EmailConfig() {}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

}
