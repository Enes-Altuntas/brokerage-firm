package com.inghubs.model.dto;

import com.inghubs.model.dto.enums.RoleType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserMetadataDTO {

  private String id;
  private RoleType role;

}
