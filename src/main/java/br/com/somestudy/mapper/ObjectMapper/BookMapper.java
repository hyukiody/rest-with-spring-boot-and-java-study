package br.com.somestudy.mapper.ObjectMapper;

import br.com.somestudy.model.Book;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import br.com.somestudy.data.vo.v1.BookVO;
import br.com.somestudy.mapper.MyModelMapper;

public class BookMapper {

	private static final ModelMapper modelMapper = new ModelMapper();

	static {
        // 1. Mapping 'key' (BookVO) to 'id' (Book)
        modelMapper.addMappings(new PropertyMap<BookVO, Book>() {
            @Override
            protected void configure() {
                map().setId(source.getKey()); // Maps BookVO's 'key' to Book's 'id'
                // Or, using the more concise method reference with typeMap():
                // modelMapper.typeMap(BookVO.class, Book.class).addMapping(BookVO::getKey, Book::setId);
            }
        });

        // 2. Automatic Mapping (Other Fields)
        // 'author', 'launchDate', 'price', and 'title' will be mapped automatically
        // if the field names are the same in BookVO and Book.  No configuration
        // is needed for these unless you have different formats or custom logic.

        // Example: Mapping back from Book to BookVO (if needed)
        modelMapper.typeMap(Book.class, BookVO.class).addMapping(Book::getId, BookVO::setKey);

        // or using typeMap():
        // modelMapper.typeMap(Book.class, BookVO.class).addMapping(Book::getId, BookVO::setKey);
    }

	public static Book mapToEntity(BookVO vo) {
		return modelMapper.map(vo, Book.class);
	}

	public static BookVO mapToVO(Book entity) {
		return modelMapper.map(entity, BookVO.class);
	}
}