package pt.ul.fc.css.project.server.business.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Converter for LocalDateTime to Timestamp and vice-versa. */
@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

  /**
   * Converts a LocalDateTime to a Timestamp.
   *
   * @param attribute the LocalDateTime to be converted
   * @return the converted Timestamp
   */
  @Override
  public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
    return attribute == null ? null : Timestamp.valueOf(attribute);
  }

  /**
   * Converts a Timestamp to a LocalDateTime.
   *
   * @param dbData the Timestamp to be converted
   * @return the converted LocalDateTime
   */
  @Override
  public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
    return dbData == null ? null : dbData.toLocalDateTime();
  }
}
