package com.example.orbixapi.controller;

import com.example.orbixapi.dto.CreateExtensionRequest;
import com.example.orbixapi.dto.ExtensionResponse;
import com.example.orbixapi.service.RentaExtensionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentaExtensionController {

    private final RentaExtensionService extensionService;

    public RentaExtensionController(RentaExtensionService extensionService) {
        this.extensionService = extensionService;
    }

    @PostMapping("/{rentalId}/extensions")
    @ResponseStatus(HttpStatus.CREATED)
    public ExtensionResponse createExtension(
            @PathVariable Long rentalId,
            @Valid @RequestBody CreateExtensionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return extensionService.createExtensionRequest(rentalId, request, userDetails.getUsername());
    }

    @PatchMapping("/extensions/{extensionId}/approve")
    public ExtensionResponse approveExtension(
            @PathVariable Long extensionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return extensionService.approveExtension(extensionId, userDetails.getUsername());
    }

    @PatchMapping("/extensions/{extensionId}/reject")
    public ExtensionResponse rejectExtension(
            @PathVariable Long extensionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return extensionService.rejectExtension(extensionId, userDetails.getUsername());
    }

    @GetMapping("/{rentalId}/extensions")
    public List<ExtensionResponse> getExtensionsForRental(
            @PathVariable Long rentalId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return extensionService.getExtensionsForRental(rentalId, userDetails.getUsername());
    }

    @GetMapping("/extensions/mine")
    public List<ExtensionResponse> getMyExtensions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return extensionService.getMyExtensions(userDetails.getUsername());
    }

    @GetMapping("/extensions/received")
    public List<ExtensionResponse> getReceivedExtensions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return extensionService.getReceivedExtensions(userDetails.getUsername());
    }
}
