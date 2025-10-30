package com.inghubs.adapters.order.elastic.criteria;

import com.inghubs.adapters.order.rest.model.OrderFilterRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.data.elasticsearch.core.query.Criteria;

@UtilityClass
public class OrderCriteriaBuilder {

  public static final String CUSTOMER_ID = "customerId";
  public static final String STATUS = "status";
  public static final String ASSET_NAME = "assetName";
  public static final String ID = "id";

  public static Criteria buildCriteria(OrderFilterRequest filterRequest) {
    List<Criteria> criteriaList = new ArrayList<>();

    if (filterRequest.getOrderId() != null) {
      criteriaList.add(Criteria.where(ID)
          .is(filterRequest.getOrderId()));
    }

    if (filterRequest.getCustomerId() != null) {
      criteriaList.add(Criteria.where(CUSTOMER_ID)
          .is(filterRequest.getCustomerId()));
    }

    if (filterRequest.getStatus() != null) {
      criteriaList.add(Criteria.where(STATUS).is(filterRequest.getStatus()));
    }

    if (filterRequest.getAssetName() != null) {
      criteriaList.add(Criteria.where(ASSET_NAME).is(filterRequest.getAssetName()));
    }


    return criteriaList.isEmpty()
        ? new Criteria()
        : criteriaList.stream().reduce(Criteria::and).orElse(new Criteria());
  }
}
