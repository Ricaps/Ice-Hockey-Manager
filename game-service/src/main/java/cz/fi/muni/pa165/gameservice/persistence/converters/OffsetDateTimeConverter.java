package cz.fi.muni.pa165.gameservice.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Converter(autoApply = true)
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, OffsetDateTime> {

	@Override
	public OffsetDateTime convertToDatabaseColumn(OffsetDateTime attribute) {
		return attribute == null ? null : attribute.truncatedTo(ChronoUnit.MILLIS);
	}

	@Override
	public OffsetDateTime convertToEntityAttribute(OffsetDateTime dbData) {
		return dbData == null ? null : dbData.truncatedTo(ChronoUnit.MILLIS);
	}

}
