package com.inghubs.adapters.asset.rest;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import com.inghubs.adapters.asset.jpa.service.AssetQueryService;
import com.inghubs.adapters.asset.rest.model.request.AssetFilterRequest;
import com.inghubs.adapters.asset.rest.model.response.AssetResponse;
import com.inghubs.common.rest.base.BaseController;
import com.inghubs.common.rest.model.GenericResponse;
import com.inghubs.common.rest.model.PaginationRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/asset")
public class AdminAssetController extends BaseController {

  private final AssetQueryService assetQueryService;

  @GetMapping("")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenericResponse<AssetResponse>> adminFetchAsset(
      @RequestParam(name = "customerId") UUID customerId,
      @RequestParam(name = "assetName") String assetName) {

    AssetFilterRequest assetFilterRequest = AssetFilterRequest.builder()
        .customerId(customerId)
        .assetName(assetName)
        .build();
    AssetEntity query = assetQueryService.query(assetFilterRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(respond(AssetResponse.toResponse(query)));
  }

  @GetMapping("/list")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenericResponse<Page<AssetResponse>>> adminFetchAssets(
      @ModelAttribute @Valid AssetFilterRequest filterRequest,
      @ModelAttribute @Valid PaginationRequest paginationRequest
  ) {

    Page<AssetEntity> query = assetQueryService.query(filterRequest, paginationRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(respond(query.map(AssetResponse::toResponse)));
  }
}
