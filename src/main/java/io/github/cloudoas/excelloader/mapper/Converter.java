package io.github.cloudoas.excelloader.mapper;

public interface Converter<S,T> {
	T convert(S source) throws Exception;
}
