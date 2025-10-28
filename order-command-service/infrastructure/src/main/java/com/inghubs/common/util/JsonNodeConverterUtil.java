package com.inghubs.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JsonNodeConverterUtil implements AttributeConverter<JsonNode, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(JsonNode jsonNode) {
    try {
      return jsonNode == null ? null : objectMapper.writeValueAsString(jsonNode);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public JsonNode convertToEntityAttribute(String dbData) {
    try {
      return dbData == null ? null : objectMapper.readTree(dbData);
    } catch (Exception e) {
      return null;
    }
  }
}