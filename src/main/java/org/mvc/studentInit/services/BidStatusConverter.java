package org.mvc.studentInit.services;

import org.mvc.studentInit.model.BidStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BidStatusConverter implements AttributeConverter<BidStatus, String> {

    @Override
    public String convertToDatabaseColumn(BidStatus attribute) {
        return attribute.toString();
    }

    @Override
    public BidStatus convertToEntityAttribute(String dbData) {
        return BidStatus.getByName(dbData.toUpperCase());
    }
}
