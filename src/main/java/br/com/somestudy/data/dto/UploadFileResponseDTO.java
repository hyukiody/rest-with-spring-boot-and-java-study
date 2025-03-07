package br.com.somestudy.data.dto;

import java.io.Serializable;
import java.util.Objects;

public class UploadFileResponseDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private String	fileDownloadDir;
	private String fileType;
	private long size;

	public UploadFileResponseDTO() {}

	public UploadFileResponseDTO(String fileName, String fileDownloadDir, String fileType, long size) {
		super();
		this.fileName = fileName;
		this.fileDownloadDir = fileDownloadDir;
		this.fileType = fileType;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDownloadDir() {
		return fileDownloadDir;
	}

	public void setFileDownloadDir(String fileDownloadDir) {
		this.fileDownloadDir = fileDownloadDir;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fileDownloadDir, fileName, fileType, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadFileResponseDTO other = (UploadFileResponseDTO) obj;
		return Objects.equals(fileDownloadDir, other.fileDownloadDir) && Objects.equals(fileName, other.fileName)
				&& Objects.equals(fileType, other.fileType) && size == other.size;
	};
	
	
}
