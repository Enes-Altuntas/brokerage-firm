package com.inghubs.adapters.asset.jpa.specification;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import com.inghubs.adapters.asset.rest.model.request.AssetFilterRequest;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class AssetSpecification {

  private static final String CUSTOMER_ID = "customerId";
  private static final String ASSET_NAME = "assetName";
  public static final String ID = "id";

  public static Specification<AssetEntity> filterAssets(AssetFilterRequest filterRequest) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filterRequest.customerId() != null) {
        predicates.add(cb.equal(root.get(ID).get(CUSTOMER_ID), filterRequest.customerId()));
      }

      if (filterRequest.assetName() != null && !filterRequest.assetName().isBlank()) {
        predicates.add(cb.equal(root.get(ID).get(ASSET_NAME), filterRequest.assetName()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
