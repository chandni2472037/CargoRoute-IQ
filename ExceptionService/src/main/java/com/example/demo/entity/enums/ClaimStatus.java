package com.example.demo.entity.enums;

public enum ClaimStatus {
	OPEN,        // Claim has been filed by the shipper or dispatcher
    UNDER_REVIEW, // Being investigated by the insurance or billing department
    SETTLED,     // Claim has been paid or resolved
    DENIED,      // Claim was rejected after investigation
    CANCELLED    // Claim was withdrawn
}
