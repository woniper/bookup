package io.bookup.common.mapper;

import org.modelmapper.ModelMapper;

/**
 * Created by woniper on 2017. 1. 11..
 */
public class DomainMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static <T> T map(Object source, Class<T> dtoClass) {
        return modelMapper.map(source, dtoClass);
    }

}