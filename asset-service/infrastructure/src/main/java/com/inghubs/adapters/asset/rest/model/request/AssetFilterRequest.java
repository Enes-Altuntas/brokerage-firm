package com.inghubs.adapters.asset.rest.model.request;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AssetFilterRequest(

    UUID customerId,

    String assetName

) {

}
