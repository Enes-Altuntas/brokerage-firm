package com.inghubs.adapters.asset.rest.model.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetFilterRequest {

  private UUID customerId;

  private String assetName;

}
