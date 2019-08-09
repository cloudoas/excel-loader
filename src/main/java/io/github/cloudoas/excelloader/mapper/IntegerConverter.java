package io.github.cloudoas.excelloader.mapper;

public class IntegerConverter implements Converter<Object, Integer> {

	@Override
	public Integer convert(Object source) throws Exception{
		return Integer.valueOf(source.toString());
	}

}
